package com.ashir.filament3drender

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceView
import com.google.android.filament.utils.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.FileChannel.open

/*
Purpose of this class is to provide util function to simplify the usage of modelviewer
& to provide Objects containing required feature for multiple instantiation
 */
class ModelViewerHelper(private val context: Context, private var surfaceView : SurfaceView,private var frameCallback : Choreographer.FrameCallback, private val setTransparent: Boolean = true) {
    lateinit var choreographer: Choreographer
    lateinit var modelViewer: ModelViewer
    private lateinit var assetFile : String
    private lateinit var assetDir : String
    private  lateinit var envFile : String

    init {
        initializeModelviewer()
    }

    private fun initializeModelviewer(){
        choreographer = Choreographer.getInstance()
        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)
    }

    fun loadFiles(fileType: FileType = FileType.GLTF, asset: String, assetDir: String? = null, envFile: String = "venetian_crossroads_2k"){
        if(fileType == FileType.GLTF){
            if(assetDir.isNullOrEmpty()) return
            assetFile = asset
            this.assetDir = assetDir
            loadGltf(assetFile)
        }else{
            assetFile = asset
            loadGlb(assetFile)
        }
        loadEnvironment(envFile)
    }

    private fun loadGlb(name: String) {
        //val buffer = readAsset("models/${name}.glb")
        val buffer = readAsset("${assetFile}")

        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()
        modelViewer.asset?.apply {
//            val degrees = 20f * seconds.toFloat()
//            val zAxis = Float3(0f, 0f, 1f)
            // 0.41987148
            // 0.027406849 perfect
            val rootTransform = this.root.getTransform()
            val number = 0f
            val zAxis = Float3(number, number, number)
            Log.d("Ashir","$zAxis" )
            this.root.setTransform(rootTransform * translation(zAxis))
        }
    }

    private fun loadEnvironment(ibl: String) {
        // Create the indirect light source and add it to the scene.
        var buffer = readAsset("envs/$ibl/${ibl}_ibl.ktx")
        KTXLoader.createIndirectLight(modelViewer.engine, buffer).apply {
            intensity = 50_000f
            modelViewer.scene.indirectLight = this
        }

        // Create the sky   and add it to the scene.
        buffer = readAsset("envs/$ibl/${ibl}_skybox.ktx")
        KTXLoader.createSkybox(modelViewer.engine, buffer).apply {
            modelViewer.scene.skybox = this
        }
    }

    private fun loadGltf(name: String) {
        //val buffer = readAsset("models/${name}.gltf")
        //modelViewer.loadModelGltf(buffer) { uri -> readAsset("models/$uri") }

        val buffer = readAsset("$assetFile")
        modelViewer.loadModelGltf(buffer) { uri -> readAsset("${assetDir}$uri") }

        modelViewer.transformToUnitCube()
    }

    private fun readAsset(assetName: String): ByteBuffer {
        val input = context.assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }


    private val defaultFrameCallback =  object: Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewer.render(currentTime)

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
    private fun Int.getTransform(): Mat4 {
        val tm = modelViewer.engine.transformManager
        val mat : FloatArray? = null
        return Mat4.of(*tm.getTransform(tm.getInstance(this), mat))
    }

    private fun Int.setTransform(mat: Mat4) {
        val tm = modelViewer.engine.transformManager
        tm.setTransform(tm.getInstance(this), mat.toFloatArray())
    }

    fun postFrameCallback(){
        choreographer.postFrameCallback(frameCallback)
    }

    fun removeFrameCallback(){
        choreographer.removeFrameCallback(frameCallback)
    }

}

enum class FileType {
    GLTF,
    GLB
}