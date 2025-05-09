package dev.capture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.adapter.DevDataAdapterExt
import dev.capture.base.BaseDevHttpViewHolder
import dev.capture.compiler.databinding.DevHttpCaptureCaptureFileAdapterBinding
import dev.capture.model.Items
import dev.utils.app.helper.view.ViewHelper

/**
 * detail: DevHttpCapture 抓包数据详情适配器
 * @author Ttt
 */
internal class AdapterCaptureFile :
    DevDataAdapterExt<Items.FileItem, BaseDevHttpViewHolder<DevHttpCaptureCaptureFileAdapterBinding>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDevHttpViewHolder<DevHttpCaptureCaptureFileAdapterBinding> {
        return BaseDevHttpViewHolder(
            DevHttpCaptureCaptureFileAdapterBinding.inflate(
                LayoutInflater.from(mContext), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BaseDevHttpViewHolder<DevHttpCaptureCaptureFileAdapterBinding>,
        position: Int
    ) {
        val item = getDataItem(position)
        ViewHelper.get()
            .setText(item.title, holder.binding.vidTitleTv)
            .setText(item.value, holder.binding.vidValueTv)
            .setOnClick({
                callback?.callback(item)
            }, holder.binding.vidCopyTv)
    }
}