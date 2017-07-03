package com.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.hls.kotlin.HLSBaseItem
import com.hls.kotlin.HLSManager
import com.hls.kotlin.ObserverManager
import com.hls.kotlin.SampleObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HLSManager.init(this)
        start.setOnClickListener {
            var url = edit_text.text
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(baseContext, "url is null", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var observer = ObserverManager.getExistObserver(url.toString())
            if (observer == null) {
                observer = object : SampleObserver() {

                    override fun update(downloader: HLSBaseItem) {
                        super.update(downloader)
                        runOnUiThread {
                            progressBar.progress = downloader.getProgress().toInt()
                            bar_text.text = "${downloader.getProgress()}%"
                        }
                    }

                }
                ObserverManager.addNewObserver(url.toString(), observer)
            }
            HLSManager.startOrResumeDownloader(url.toString(), observer)
        }

        pause.setOnClickListener {
            var url = edit_text.text
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(baseContext, "url is null", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            HLSManager.pauseDownloader(url.toString())
        }

        resume.setOnClickListener {
            var url = edit_text.text
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(baseContext, "url is null", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var observer = ObserverManager.getExistObserver(url.toString())
            if (observer == null) {
                observer = object : SampleObserver() {

                    override fun update(downloader: HLSBaseItem) {
                        super.update(downloader)
                        runOnUiThread {
                            progressBar.progress = downloader.getProgress().toInt()
                            bar_text.text = "${downloader.getProgress()}%"
                        }
                    }

                }
                ObserverManager.addNewObserver(url.toString(), observer as SampleObserver)
            }
            HLSManager.startOrResumeDownloader(url.toString(), observer!!)
        }

        delete.setOnClickListener {
            var url = edit_text.text
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(baseContext, "url is null", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            HLSManager.deleteDownloader(url.toString())
        }
    }
}
