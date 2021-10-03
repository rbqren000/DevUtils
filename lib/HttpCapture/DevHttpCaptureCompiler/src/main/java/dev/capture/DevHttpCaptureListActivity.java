package dev.capture;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import dev.callback.DevCallback;
import dev.capture.compiler.R;
import dev.capture.compiler.databinding.DevHttpCaptureListActivityBinding;
import dev.utils.DevFinal;
import dev.utils.app.BarUtils;
import dev.utils.app.ResourceUtils;
import dev.utils.app.ViewUtils;
import dev.utils.app.toast.ToastTintUtils;

/**
 * detail: DevHttpCapture 抓包数据列表
 * @author Ttt
 */
public class DevHttpCaptureListActivity
        extends BaseDevHttpActivity {

    private       DevHttpCaptureListActivityBinding mBinding;
    // 当前选中的 Module
    private       String                            mModule;
    // 当前选中的日期
    private       String                            mDate;
    // 首页适配器
    private final AdapterDateModuleList             mAdapter  = new AdapterDateModuleList();
    // 查询回调
    private final DevCallback<Boolean>              mCallback = new DevCallback<Boolean>() {
        @Override
        public void callback(
                Boolean isQuerying,
                int size
        ) {
            if (!isFinishing()) {
                if (isQuerying) {
                    if (size == 0) {
                        ToastTintUtils.normal(
                                ResourceUtils.getString(R.string.dev_http_capture_querying)
                        );
                    }
                    return;
                }
                ToastTintUtils.success(
                        ResourceUtils.getString(R.string.dev_http_capture_query_complete)
                );
                // 设置数据源
                mAdapter.setDataList(
                        UtilsCompiler.getInstance().getDateData(
                                mModule, mDate
                        )
                );
                // 判断是否存在数据
                ViewUtils.reverseVisibilitys(
                        mAdapter.isDataNotEmpty(),
                        mBinding.vidLinear,
                        mBinding.vidTips.vidTipsFrame
                );
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DevHttpCaptureListActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // 设置状态栏颜色
        BarUtils.setStatusBarColor(this, ResourceUtils.getColor(R.color.dev_http_capture_title_bg_color));
        // 初始化数据
        initValue(getIntent());
    }

    @Override
    public void onBackPressed() {
        finishOperate();
    }

    /**
     * 销毁操作方法
     */
    private void finishOperate() {
        // 移除回调
        UtilsCompiler.getInstance().removeCallback(mCallback);
        // 关闭当前页面
        finish();
    }

    // ==========
    // = 内部方法 =
    // ==========

    /**
     * 初始化数据
     * @param intent Intent
     */
    private void initValue(final Intent intent) {
        // 获取模块名
        mModule = intent.getStringExtra(DevFinal.MODULE);
        // 获取时间
        mDate = intent.getStringExtra(DevFinal.DATE);

        // 设置点击事件
        mBinding.vidTitle.vidBackIgview.setOnClickListener(view -> finishOperate());
        // 设置标题
        mBinding.vidTitle.vidTitleTv.setText(mDate + " - " + mModule);
        // 设置提示文案
        mBinding.vidTips.vidTipsTv.setText(R.string.dev_http_capture_query_no_data);
        // 绑定适配器
        mAdapter.bindAdapter(mBinding.vidRecycler);

        // ==========
        // = 数据获取 =
        // ==========

        UtilsCompiler.getInstance().queryData(
                mCallback, false
        );
    }
}