package com.ashlikun.waveview.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        rhythmView.setPerHeight(0.9f)
    }

    fun onClick2(view: View) {

        waveLineView.setVolume(100)
        waveLineView.startAnim()
    }
}