package afkt.project.feature

import afkt.project.R
import afkt.project.base.app.BaseActivity
import afkt.project.data_model.button.ButtonList
import afkt.project.data_model.button.ButtonValue
import afkt.project.data_model.button.RouterPath
import afkt.project.databinding.BaseViewRecyclerviewBinding
import com.therouter.router.Route
import dev.callback.DevItemClickCallback
import dev.utils.app.toast.ToastTintUtils

/**
 * detail: Module 列表 Activity
 * @author Ttt
 */
@Route(path = RouterPath.ModuleActivity_PATH)
class ModuleActivity : BaseActivity<BaseViewRecyclerviewBinding>() {

    override fun baseLayoutId(): Int = R.layout.base_view_recyclerview

    override fun initValue() {
        super.initValue()
        // 初始化布局管理器、适配器
        ButtonAdapter(ButtonList.getModuleButtonValues(moduleType))
            .setItemCallback(object : DevItemClickCallback<ButtonValue>() {
                override fun onItemClick(
                    buttonValue: ButtonValue,
                    param: Int
                ) {
                    when (buttonValue.type) {
                        ButtonValue.BTN_ROOM,
                        ButtonValue.BTN_GREEN_DAO -> ToastTintUtils.info(
                            "具体请查看: 【DevUtils-repo】application/AppDB"
                        )

                        ButtonValue.BTN_EVENT_BUS,
                        ButtonValue.BTN_GLIDE,
                        ButtonValue.BTN_IMAGE_LOADER,
                        ButtonValue.BTN_GSON,
                        ButtonValue.BTN_FASTJSON,
                        ButtonValue.BTN_ZXING,
                        ButtonValue.BTN_PICTURE_SELECTOR,
                        ButtonValue.BTN_OKGO,
                        ButtonValue.BTN_LUBAN,
                        ButtonValue.BTN_MMKV,
                        ButtonValue.BTN_WORK_MANAGER -> ToastTintUtils.info(
                            "具体请搜索: 【DevUtils-repo】lib/LocalModules/DevOther " + buttonValue.text
                        )

                        else -> routerActivity(buttonValue)
                    }
                }
            }).bindAdapter(binding.vidRv)
        // 注册观察者
        registerAdapterDataObserver(binding.vidRv, true)
    }
}