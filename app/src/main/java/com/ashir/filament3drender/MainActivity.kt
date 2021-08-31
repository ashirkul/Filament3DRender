package com.ashir.filament3drender

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceView
import android.widget.TextView
import com.ashir.filament3drender.databinding.ActivityMainBinding
import com.google.android.filament.utils.*
import com.google.android.filament.utils.R
import java.nio.ByteBuffer
import kotlin.random.Random

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    /**
     * A native method that is implemented by the 'filament3drender' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

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
    }


    private fun setOnClickListener(){
        binding.fullScreenRender.setOnClickListener {
            val intent = Intent(this,FullScreenHikemoji::class.java)
            startActivity(intent)
        }
        binding.multipleRender.setOnClickListener {
        }
        binding.transparentRender.setOnClickListener {
            val intent = Intent(this,TransparentRenderActivity::class.java)
            startActivity(intent)
        }
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