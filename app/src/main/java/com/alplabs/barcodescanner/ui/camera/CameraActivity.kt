package com.alplabs.barcodescanner.ui.camera

import android.os.Bundle
import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import androidx.core.app.ActivityCompat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.media.ImageReader.OnImageAvailableListener
import android.media.ImageReader.newInstance
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alplabs.barcodescanner.R
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.databinding.ActivityCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


class CameraActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1

        const val WIDTH = 1280
        const val HEIGHT = 720

    }

    private var cameraDevice: CameraDevice? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var captureSession: CameraCaptureSession? = null

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    private var imageReader: ImageReader? = null

    private var currentImage: Image? = null

    private val cameraOpenCloseLock = Semaphore(1)

    private lateinit var binding: ActivityCameraBinding
    private lateinit var viewModel: CameraViewModel

    private val surface: Surface by lazy {
        binding.textureView.surfaceTexture?.setDefaultBufferSize(WIDTH, HEIGHT)
        Surface(binding.textureView.surfaceTexture)
    }


    private val textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            //open your camera here
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            // Transform you image captured size according to the surface width and height
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }
    }


    private val deviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraOpenCloseLock.release()
            cameraDevice?.close()
            cameraDevice = null
        }
    }


    private val captureSessionStateCallback =  object : CameraCaptureSession.StateCallback() {

        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            if (null == cameraDevice) {
                return
            }

            captureSession = cameraCaptureSession
            updatePreview()
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
//            showToast("Configuration failed")
        }

    }

    private val imageAvailableListener = OnImageAvailableListener { reader ->
        backgroundHandler?.post {
            try {
                if (currentImage == null) {
                    currentImage = reader.acquireLatestImage()
                    startInvoiceDetector(currentImage!!)
                }
            } catch (th: Throwable) {
                Log.e("imageAvailableListener", "Ao acquire latest image", th)
            }
        }

    }


    private fun startInvoiceDetector(image: Image) {
        viewModel.startScanner(this@CameraActivity, image)
    }

    private fun saveBarcodeModel(barcodeModel: BarcodeModel) {
        lifecycleScope.async(context = Dispatchers.IO) {
            viewModel.addBarcodeModel(barcodeModel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val anim = AnimationUtils.loadAnimation(this, R.anim.translate_divider_camera)
        anim.repeatCount = Animation.INFINITE
        binding.divider.startAnimation(anim)

        viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)

        binding.imgBtnClose.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }


        viewModel.foundedBarcodeModel.observe(this) { barcodeModel ->
            if (barcodeModel != null) {
                saveBarcodeModel(barcodeModel)
            } else {
                currentImage?.close()
                currentImage = null
            }
        }

        viewModel.addedBarcodeModel.observe(this) {
            if (it.isSuccess) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                currentImage?.close()
                currentImage = null
            }
        }
    }

    override fun onResume() {
        super.onResume()

        startBackgroundThread()

        if (binding.textureView.isAvailable) {
            openCamera()
        } else {
            binding.textureView.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()

        super.onPause()
    }


    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("camera_background").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()

        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(CameraActivity::stopBackgroundThread.name, "Join thread for quit", e)
        }

    }

    private fun createCameraPreview() {

        try {

            imageReader = newInstance(WIDTH, HEIGHT, android.graphics.ImageFormat.YUV_420_888, 24)
            imageReader?.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)
            val imageReaderSurface = imageReader!!.surface


            captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)
            captureRequestBuilder?.addTarget(imageReaderSurface)

            cameraDevice?.createCaptureSession(listOf(surface, imageReaderSurface), captureSessionStateCallback, null)

        } catch (e: CameraAccessException) {
            Log.e(CameraActivity::createCameraPreview.name, "Access camera was not possible", e)
        }

    }

    private fun openCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            val cameraId = manager.cameraIdList.firstOrNull() ?: return

            if (ActivityCompat.checkSelfPermission(this, permission.CAMERA) == PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )

                return
            }

            // Wait for camera to open - 2.5 seconds is sufficient
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }

            manager.openCamera(cameraId, deviceStateCallback, backgroundHandler)

        } catch (e: CameraAccessException) {
            Log.e(CameraActivity::createCameraPreview.name, "Access camera was not possible", e)
        }
    }

    private fun updatePreview() {

        val requestBuilder = captureRequestBuilder ?: return

        requestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

        try {
            captureSession?.setRepeatingRequest(requestBuilder.build(), null, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(CameraActivity::createCameraPreview.name, "Access camera was not possible", e)
        }
    }

    private fun closeCamera() {

        try {
            cameraOpenCloseLock.acquire()

            captureSession?.close()
            captureSession = null

            cameraDevice?.close()
            cameraDevice = null

            imageReader?.close()
            imageReader = null

            captureRequestBuilder = null

        } catch (e: InterruptedException) {
            Log.e(CameraActivity::closeCamera.name, "Close camera", e)
        } finally {
            cameraOpenCloseLock.release()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION) {

            if (grantResults[0] == PERMISSION_DENIED) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

        }
    }
}