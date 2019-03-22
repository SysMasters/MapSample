package cn.sysmaster.mapsample.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.sysmaster.mapsample.R;

/**
 * @author dabo
 * @date 2019/3/22
 * @describe
 */
public class BusinessView extends LinearLayout {

    ImageView mImgBusiness;
    ImageView mImgLayer;
    LinearLayout mLayoutOrderView;
    TextView mTvAddress;
    TextView mTvCanBorrowAndroidNum;
    TextView mTvCanBorrowIOSNum;
    TextView mTvCanBorrowTypeCNum;
    TextView mTvCanReturnNum;
    TextView mTvName;
    TextView mTvOpenTime;


    public BusinessView(Context context) {
        this(context, null);
    }

    public BusinessView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BusinessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_home_business, this);
        mImgBusiness = findViewById(R.id.business_image);
        mTvName = findViewById(R.id.name);
        mTvOpenTime = findViewById(R.id.opening_hours);
        mTvAddress = findViewById(R.id.address);
        mTvCanBorrowIOSNum = findViewById(R.id.can_borrow_ios_num);
        mTvCanBorrowAndroidNum = findViewById(R.id.can_borrow_android_num);
        mTvCanBorrowTypeCNum = findViewById(R.id.can_borrow_typec_num);
        mTvCanReturnNum = findViewById(R.id.can_return_num);
    }
}