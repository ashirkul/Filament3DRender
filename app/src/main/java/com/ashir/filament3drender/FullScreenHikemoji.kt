package com.ashir.filament3drender

import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.filament.android.UiHelper
import com.google.android.filament.utils.*
import java.nio.ByteBuffer

class FullScreenHikemoji : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var modelViewerHelper: ModelViewerHelper
    private val fpsCounter = FPSCounter();
    private var fileTypePos : Int = -1
    private var filePos : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_hikemoji)
        surfaceView = findViewById(R.id.surface_view)
        modelViewerHelper = ModelViewerHelper(this,surfaceView,frameCallback,setTransparent = false)
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

//            // Reset the root transform, then rotate it around the Z axis.
//            modelViewer.asset?.apply {
//                modelViewer.transformToUnitCube()
//                val rootTransform = this.root.getTransform()
//                val degrees = 20f * seconds.toFloat()
//                val zAxis = Float3(0f, 1f, 0f)
//                this.root.setTransform(rootTransform * rotation(zAxis, degrees))
//            }
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