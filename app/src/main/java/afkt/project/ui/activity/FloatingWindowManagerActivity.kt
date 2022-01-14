package afkt.project.ui.activity

import afkt.project.R
import afkt.project.base.app.BaseActivity
import afkt.project.base.config.RouterPath
import afkt.project.databinding.BaseViewRecyclerviewBinding
import afkt.project.model.item.ButtonList
import afkt.project.model.item.ButtonValue
import afkt.project.ui.adapter.ButtonAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import dev.DevUtils
import dev.callback.DevItemClickCallback
import dev.utils.app.assist.floating.DevFloatingTouchIMPL
import dev.utils.app.assist.floating.FloatingWindowManagerAssist
import dev.utils.app.assist.floating.IFloatingTouch
import dev.utils.app.logger.DevLogger
import dev.utils.app.toast.ToastUtils

/**
 * detail: 悬浮窗管理辅助类 ( 需权限 )
 * @author Ttt
 */
@Route(path = RouterPath.FloatingWindowManagerActivity_PATH)
class FloatingWindowManagerActivity : BaseActivity<BaseViewRecyclerviewBinding>() {

    override fun baseLayoutId(): Int = R.layout.base_view_recyclerview

    override fun initValue() {
        super.initValue()

        // 初始化布局管理器、适配器
        ButtonAdapter(ButtonList.floatingWindowButtonValues)
            .setItemCallback(object : DevItemClickCallback<ButtonValue>() {
                override fun onItemClick(
                    buttonValue: ButtonValue,
                    param: Int
                ) {
                    when (buttonValue.type) {
                        ButtonValue.BTN_OPEN_FLOATING_WINDOW -> {
                            if (checkOverlayPermission()) {
                                Utils.instance.addView()
                            }
                        }
                        ButtonValue.BTN_CLOSE_FLOATING_WINDOW -> {
                            Utils.instance.removeView()
                        }
                    }
                }
            }).bindAdapter(binding.vidRv)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (FloatingWindowManagerAssist.isOverlayRequestCode(requestCode)) {
            checkOverlayPermission()
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return if (FloatingWindowManagerAssist.checkOverlayPermission(this, true)) {
            true
        } else {
            ToastUtils.showShort("请先开启悬浮窗权限")
            false
        }
    }
}

// ==========
// = 实现代码 =
// ==========

/**
 * detail: 悬浮窗工具类
 * @author Ttt
 */
internal class Utils private constructor() {

    companion object {

        val instance: Utils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Utils()
        }
    }

    // 悬浮窗管理辅助类
    private val mAssist = FloatingWindowManagerAssist()

    // 悬浮窗触摸辅助类实现
    private val mTouchAssist: IFloatingTouch by lazy {
        object : DevFloatingTouchIMPL() {
            override fun updateViewLayout(
                view: View?,
                dx: Int,
                dy: Int
            ) {
                mAssist.updateViewLayout(view, dx, dy)
            }
        }
    }

    // 悬浮 View
    private val mFloatingView: FloatingView by lazy {
        FloatingView(DevUtils.getContext(), mTouchAssist)
    }

    /**
     * 添加悬浮 View
     */
    fun addView() {
        mAssist.addView(mFloatingView)
    }

    /**
     * 移除悬浮 View
     */
    fun removeView() {
        mAssist.removeView(mFloatingView)
    }
}

/**
 * detail: 悬浮 View
 * @author Ttt
 */
@SuppressLint("ViewConstructor")
class FloatingView(
    thisContext: Context,
    private val assist: IFloatingTouch?
) : LinearLayout(thisContext) {

    init {
        initialize()
    }

    private fun initialize() {
        View.inflate(context, R.layout.layout_floating, this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        assist?.onTouchEvent(this, event)
        return true
    }

    // =

    private val TAG = FloatingView::class.java.simpleName

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        DevLogger.dTag(TAG, "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        DevLogger.dTag(TAG, "onDetachedFromWindow")
    }
}