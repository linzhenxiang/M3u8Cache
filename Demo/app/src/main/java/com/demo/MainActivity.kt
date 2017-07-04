package com.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.hls.kotlin.HLSBaseItem
import com.hls.kotlin.HLSManager
import com.hls.kotlin.ObserverManager
import com.hls.kotlin.SampleObserver
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


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
            doAsync {
                HLSManager.deleteDownloader(url.toString())
                uiThread {
                    progressBar.progress = 0
                    bar_text.text = "0%"
                    Toast.makeText(baseContext, "file has be deleted", Toast.LENGTH_SHORT).show()

                }
            }
        }
        play.setOnClickListener {
            var url = edit_text.text
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(baseContext, "url is null", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_VIEW)
            val type = "video/mp4"
            val uri = Uri.parse(HLSManager.getDownloadFilePath(url.toString()))
            intent.setDataAndTypeAndNormalize(uri, type)
            startActivity(intent)
        }
    }
}
