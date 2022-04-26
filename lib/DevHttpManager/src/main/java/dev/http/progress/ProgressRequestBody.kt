package dev.http.progress

import android.os.Handler
import dev.DevUtils
import dev.utils.LogPrintUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * detail: 进度监听请求体 RequestBody
 * @author Ttt
 * 通过此类获取 OkHttp 请求体数据处理进度, 可以处理任何的 RequestBody
 */
open class ProgressRequestBody(
    // 原数据请求体
    protected val delegate: RequestBody,
    // 上传、下载回调接口
    protected val callback: Progress.Callback?,
    // 回调 UI 线程通知 ( 如果为 null 则会非 UI 线程通知 )
    protected val handler: Handler? = DevUtils.getHandler(),
    // 回调刷新时间 ( 毫秒 ) - 小于等于 0 则每次进度变更都进行通知
    protected val refreshTime: Long = Progress.REFRESH_TIME
) : RequestBody() {

    // 日志 TAG
    protected val TAG = ProgressRequestBody::class.java.simpleName

    // ===============
    // = RequestBody =
    // ===============

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        return try {
            delegate.contentLength()
        } catch (e: IOException) {
            LogPrintUtils.eTag(TAG, e, "contentLength")
            -1L
        }
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink = countingSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()

        countingSink.progress.toFinish()
            .callback(callback, handler)
    }

    // ============
    // = 内部包装类 =
    // ============

    /**
     * detail: 内部进度监听包装类
     * @author Ttt
     */
    inner class CountingSink(sink: Sink) : ForwardingSink(sink) {

        // 进度信息存储类
        val progress = Progress()

        init {
            progress.setTotalSize(contentLength())
                .toStart().callback(callback, handler)
        }

        // ==================
        // = ForwardingSink =
        // ==================

        @Throws(IOException::class)
        override fun write(
            source: Buffer,
            byteCount: Long
        ) {
            try {
                super.write(source, byteCount)
            } catch (e: Exception) {
                progress.toError(e)
                    .callback(callback, handler)
                throw e
            }
            if (progress.getTotalSize() <= 0) {
                progress.setTotalSize(contentLength())
            }
            val allowCallback = changeProgress(
                progress, refreshTime, byteCount
            )
            if (allowCallback) {
                progress.toIng()
                    .callback(callback, handler)
            }
        }
    }
}