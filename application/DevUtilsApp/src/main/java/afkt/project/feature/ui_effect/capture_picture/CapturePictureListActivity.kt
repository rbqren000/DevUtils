package afkt.project.feature.ui_effect.capture_picture

import afkt.project.R
import afkt.project.base.project.BaseProjectActivity
import afkt.project.base.project.BaseProjectViewModel
import afkt.project.data_model.bean.AdapterBean
import afkt.project.data_model.bean.AdapterBean.Companion.newAdapterBeanList
import afkt.project.data_model.button.RouterPath
import afkt.project.databinding.ActivityCapturePictureListBinding
import afkt.project.databinding.AdapterCapturePictureBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.therouter.router.Route
import dev.base.DevSource
import dev.base.widget.BaseTextView
import dev.engine.DevEngine
import dev.engine.storage.OnDevInsertListener
import dev.engine.storage.StorageItem
import dev.engine.storage.StorageResult
import dev.utils.app.CapturePictureUtils
import dev.utils.app.ResourceUtils
import dev.utils.app.helper.quick.QuickHelper
import dev.utils.app.helper.view.ViewHelper
import dev.utils.common.FileUtils

/**
 * detail: CapturePictureUtils ListView 截图
 * @author Ttt
 */
@Route(path = RouterPath.UI_EFFECT.CapturePictureListActivity_PATH)
class CapturePictureListActivity :
    BaseProjectActivity<ActivityCapturePictureListBinding, BaseProjectViewModel>(
        R.layout.activity_capture_picture_list, simple_Agile = {
            if (it is CapturePictureListActivity) {
                it.apply {
                    // 截图按钮
                    val view = QuickHelper.get(BaseTextView(this))
                        .setText("截图")
                        .setBold()
                        .setTextColors(ResourceUtils.getColor(R.color.white))
                        .setTextSizeBySp(15.0F)
                        .setPaddingLeft(30)
                        .setPaddingRight(30)
                        .setOnClick {
                            DevEngine.getStorage()?.insertImageToExternal(
                                StorageItem.createExternalItem(
                                    "list.jpg"
                                ),
                                DevSource.create(
                                    CapturePictureUtils.snapshotByListView(binding.vidLv)
                                ),
                                object : OnDevInsertListener {
                                    override fun onResult(
                                        result: StorageResult,
                                        params: StorageItem?,
                                        source: DevSource?
                                    ) {
                                        showToast(
                                            result.isSuccess(),
                                            "保存成功\n${FileUtils.getAbsolutePath(result.getFile())}",
                                            "保存失败"
                                        )
                                    }
                                }
                            )
                        }.getView<View>()
                    toolbar?.addView(view)

                    val lists = newAdapterBeanList(15)
                    // 设置适配器
                    binding.vidLv.adapter = object : BaseAdapter() {
                        override fun getCount(): Int {
                            return lists.size
                        }

                        override fun getItem(position: Int): AdapterBean {
                            return lists[position]
                        }

                        override fun getItemId(position: Int): Long {
                            return position.toLong()
                        }

                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val adapterBean = getItem(position)
                            // 初始化 View 设置 TextView
                            val itemBinding = AdapterCapturePictureBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                            ViewHelper.get()
                                .setText(adapterBean.title, itemBinding.vidTitleTv)
                                .setText(adapterBean.content, itemBinding.vidContentTv)
                            return itemBinding.root
                        }
                    }
                }
            }
        }
    )