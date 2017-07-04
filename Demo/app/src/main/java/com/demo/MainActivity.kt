package com.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.hls.kotlin.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : Activity() {

    var status = DownloadStatus.NONE
    @SuppressLint("NewApi")
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

            if (status == DownloadStatus.NONE || status == DownloadStatus.PENDING || status == DownloadStatus.PAUSED || status == DownloadStatus.ERROR) {
                startLoad(url.toString())
            } else if (status == DownloadStatus.DOWNLOADING) {
                pauseLoad(url.toString())
            }

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
                    start.text = "开始"
                    status = DownloadStatus.NONE
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


    fun startLoad(url: String) {
        var observer = ObserverManager.getExistObserver(url)
        if (observer == null) {
            observer = object : SampleObserver() {

                override fun update(downloader: HLSBaseItem) {
                    super.update(downloader)
                    if (downloader.downloadStatus == DownloadStatus.DOWNLOADING) {
                        runOnUiThread {
                            start.text = "暂停"
                            status = downloader.downloadStatus
                            progressBar.progress = downloader.getProgress().toInt()
                            bar_text.text = "${downloader.getProgress()}%"
                        }
                    } else if (downloader.downloadStatus == DownloadStatus.ERROR) {
                        runOnUiThread {
                            start.text = "失败"
                            status = downloader.downloadStatus
                            progressBar.progress = downloader.getProgress().toInt()
                            bar_text.text = "${downloader.getProgress()}%"
                            Toast.makeText(baseContext, "${downloader.fail_reason}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            }
            ObserverManager.addNewObserver(url, observer)
        }
        HLSManager.startOrResumeDownloader(url, observer)
    }

    fun pauseLoad(url: String) {
        HLSManager.pauseDownloader(url)
        start.text = "继续"
        status = DownloadStatus.PAUSED
    }
}
