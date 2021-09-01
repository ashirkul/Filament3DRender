package com.ashir.filament3drender

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.ashir.filament3drender.databinding.ActivityMainBinding
import com.google.android.filament.utils.*
import java.nio.ByteBuffer
import kotlin.random.Random

class MainActivity : Activity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    /**
     * A native method that is implemented by the 'filament3drender' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    private var fileTypePos : Int = 0
    private var filePos : Int = 0

    companion object {
        init {
            Utils.init()
            System.loadLibrary("filament3drender")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        setSpinners()
    }


    private fun setSpinners(){
        val spinner: Spinner = binding.spinner
        ArrayAdapter.createFromResource(this, R.array.file_type, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }

    }


    private fun setOnClickListener(){
        binding.fullScreenRender.setOnClickListener {
            val intent = Intent(this,FullScreenHikemoji::class.java)
            intent.putExtra(resources.getString(R.string.file_type), fileTypePos)
            intent.putExtra(resources.getString(R.string.file_pos), filePos)
            startActivity(intent)
        }
        binding.multipleRender.setOnClickListener {
        }
        binding.transparentRender.setOnClickListener {
//            val intent = Intent(this,TransparentRenderActivity::class.java)
//            startActivity(intent)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

        val spinner2: Spinner = binding.spinner2
        fileTypePos = pos
        ArrayAdapter.createFromResource(this, if(pos==0){R.array.gltf} else {R.array.glb}, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
            spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    filePos = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}

public class FPSCounter {
    var startTime = System.nanoTime();
    var frames = 0;

    public fun logFrame()
    {
        frames++;

        if (System.nanoTime() - startTime >= 1000000000) {
            Log.d("FPSCounter", "fps: " + frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }
}