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
    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer: ModelViewer
    private val fpsCounter = FPSCounter();
    private lateinit var uiHelper: UiHelper
    private var fileTypePos : Int = -1
    private var filePos : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = SurfaceView(this).apply { setContentView(this) }
        choreographer = Choreographer.getInstance()
        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)
        fileTypePos = intent.getIntExtra(resources.getString(R.string.file_type),-1)
        filePos = intent.getIntExtra(resources.getString(R.string.file_pos),-1)
        Log.d("Ashir","File type - ${fileTypePos}  | FilePos  - ${filePos}" )
//        loadGlb("")
//        loadGlb("DamagedHelmet")
//        loadGlb("DamagedHelmet")
//        loadGlb("DamagedHelmet")

//        loadGltf("BusterDrone")
        if(fileTypePos == 0){
            loadGltf("BusterDrone")
        }else{
            loadGlb("")
        }
        //modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)
        loadEnvironment("venetian_crossroads_2k")
    }

    private val frameCallback = object : Choreographer.FrameCallback {
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
        choreographer.postFrameCallback(frameCallback)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameCallback)
    }

    private fun loadGlb(name: String) {
        //val buffer = readAsset("models/${name}.glb")
        val array: Array<String> = resources.getStringArray(R.array.glb)
        val fileName = array[filePos]
        val buffer = readAsset("${fileName}")

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

    private fun readAsset(assetName: String): ByteBuffer {
        val input = assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
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

        val filePathArray : Array<String> = resources.getStringArray(R.array.gltf)
        val fileDirArray : Array<String> = resources.getStringArray(R.array.gltf_dir)
        val fileName = filePathArray[filePos]
        val fileDir = fileDirArray[filePos]
        Log.d("Ashir","File - ${fileName}  | FileDir  - ${fileDir}" )
        val buffer = readAsset("$fileName")
        modelViewer.loadModelGltf(buffer) { uri -> readAsset("${fileDir}$uri") }

        modelViewer.transformToUnitCube()
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
}