package com.ashir.filament3drender

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Choreographer
import android.view.SurfaceView

class MultipleRender : AppCompatActivity() {
    private lateinit var surfaceView1: SurfaceView
    private lateinit var surfaceView2: SurfaceView
    private lateinit var surfaceView3: SurfaceView
    private lateinit var modelViewerHelper1: ModelViewerHelper
    private lateinit var modelViewerHelper2: ModelViewerHelper
    private lateinit var modelViewerHelper3: ModelViewerHelper
    private val fpsCounter = FPSCounter()
    private var fileTypePos : Int = -1
    private var filePos : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_render)
        fileTypePos = intent.getIntExtra(resources.getString(R.string.file_type),-1)
        filePos = intent.getIntExtra(resources.getString(R.string.file_pos),-1)
        initialize()
        loadFiles()
    }

    private fun initialize(){
        surfaceView1 = findViewById(R.id.sf_view1)
        surfaceView2 = findViewById(R.id.sf_view2)
        surfaceView3 = findViewById(R.id.sf_view3)
        modelViewerHelper1 = ModelViewerHelper(this,surfaceView1,defaultFrameCallback1)
        modelViewerHelper2 = ModelViewerHelper(this,surfaceView2,defaultFrameCallback2)
        modelViewerHelper3 = ModelViewerHelper(this,surfaceView3,defaultFrameCallback3)
    }

    private fun loadFiles(){
        if(fileTypePos == 0) {
            val filePathArray : Array<String> = resources.getStringArray(R.array.gltf)
            val fileDirArray : Array<String> = resources.getStringArray(R.array.gltf_dir)
            val fileName1 = filePathArray[filePos % filePathArray.size]
            val fileName2 = filePathArray[(filePos+1) % filePathArray.size]
            val fileName3 = filePathArray[(filePos+2) % filePathArray.size]
            val fileDir1 = fileDirArray[filePos % fileDirArray.size]
            val fileDir2 = fileDirArray[(filePos+1) % fileDirArray.size]
            val fileDir3 = fileDirArray[(filePos+2) % fileDirArray.size]
            modelViewerHelper1.loadFiles(FileType.GLTF,fileName1,fileDir1)
            modelViewerHelper2.loadFiles(FileType.GLTF,fileName2,fileDir2)
            modelViewerHelper3.loadFiles(FileType.GLTF,fileName3,fileDir3)
        }else{
            val filePathArray : Array<String> = resources.getStringArray(R.array.glb)
            val fileName1 = filePathArray[filePos % filePathArray.size]
            val fileName2 = filePathArray[(filePos+1) % filePathArray.size]
            val fileName3 = filePathArray[(filePos+2) % filePathArray.size]
            modelViewerHelper1.loadFiles(FileType.GLB,fileName1)
            modelViewerHelper2.loadFiles(FileType.GLB,fileName2)
            modelViewerHelper3.loadFiles(FileType.GLB,fileName3)

        }
    }

    override fun onResume() {
        super.onResume()
        modelViewerHelper1.postFrameCallback()
        modelViewerHelper2.postFrameCallback()
        modelViewerHelper3.postFrameCallback()
    }

    override fun onPause() {
        super.onPause()
        modelViewerHelper1.removeFrameCallback()
        modelViewerHelper2.removeFrameCallback()
        modelViewerHelper3.removeFrameCallback()

    }

    override fun onStop() {
        super.onStop()
        modelViewerHelper1.removeFrameCallback()
        modelViewerHelper2.removeFrameCallback()
        modelViewerHelper3.removeFrameCallback()
    }


    private val defaultFrameCallback1 =  object: Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            modelViewerHelper1.choreographer.postFrameCallback(this)
            modelViewerHelper1.modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewerHelper1.modelViewer.render(currentTime)

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

    private val defaultFrameCallback2 =  object: Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            modelViewerHelper2.choreographer.postFrameCallback(this)
            modelViewerHelper2.modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewerHelper2.modelViewer.render(currentTime)

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

    private val defaultFrameCallback3 =  object: Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            modelViewerHelper3.choreographer.postFrameCallback(this)
            modelViewerHelper3.modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewerHelper3.modelViewer.render(currentTime)

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
}