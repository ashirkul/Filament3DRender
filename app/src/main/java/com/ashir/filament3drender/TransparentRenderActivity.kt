package com.ashir.filament3drender

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.Gravity
import android.view.Surface
import android.view.SurfaceView
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.utils.*
import java.lang.Math.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.Channels
import android.graphics.PixelFormat

import android.view.SurfaceHolder




class TransparentRenderActivity : AppCompatActivity() {
    private lateinit var surfaceView: SurfaceView
    private lateinit var modelViewerHelper: ModelViewerHelper
    private val fpsCounter = FPSCounter();
    private var fileTypePos : Int = -1
    private var filePos : Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparent_render)
        surfaceView = findViewById(R.id.surface_view1)
        modelViewerHelper = ModelViewerHelper(this,surfaceView,frameCallback, isBgOpaque = false)
        fileTypePos = intent.getIntExtra(resources.getString(R.string.file_type),-1)
        filePos = intent.getIntExtra(resources.getString(R.string.file_pos),-1)
        Log.d("Ashir","File type - ${fileTypePos}  | FilePos  - ${filePos}" )
        if(fileTypePos == 0) {
            val filePathArray : Array<String> = resources.getStringArray(R.array.gltf)
            val fileDirArray : Array<String> = resources.getStringArray(R.array.gltf_dir)
            val fileName = filePathArray[filePos]
            val fileDir = fileDirArray[filePos]
            modelViewerHelper.loadFiles(FileType.GLTF,fileName,fileDir)
        }else{
            val filePathArray : Array<String> = resources.getStringArray(R.array.glb)
            val fileName = filePathArray[filePos]
            modelViewerHelper.loadFiles(FileType.GLB,fileName)
        }
    }

    private val frameCallback = object : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            modelViewerHelper.choreographer.postFrameCallback(this)
            modelViewerHelper.modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewerHelper.modelViewer.render(currentTime)

            fpsCounter.logFrame();
        }
    }

    override fun onResume() {
        super.onResume()
        modelViewerHelper.postFrameCallback()
    }

    override fun onPause() {
        super.onPause()
        modelViewerHelper.removeFrameCallback()
    }

    override fun onDestroy() {
        super.onDestroy()
        modelViewerHelper.removeFrameCallback()
    }
}