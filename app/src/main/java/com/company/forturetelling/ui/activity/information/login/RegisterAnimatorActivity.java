package com.company.forturetelling.ui.activity.information.login;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.company.forturetelling.R;
import com.company.forturetelling.base.BaseActivity;
import com.company.forturetelling.bean.CheckPhoneBean;
import com.company.forturetelling.bean.CityBean;
import com.company.forturetelling.bean.RegisterBean;
import com.company.forturetelling.bean.bus.ExitEvent;
import com.company.forturetelling.bean.bus.WeChartEvent;
import com.company.forturetelling.global.Constants;
import com.company.forturetelling.global.HttpConstants;
import com.company.forturetelling.ui.MainActivity;
import com.company.forturetelling.ui.activity.information.RegisterActivity;
import com.company.forturetelling.utils.DeviceIdUtil;
import com.company.forturetelling.view.CountdownView;
import com.company.forturetelling.view.PasswordEditText;
import com.company.forturetelling.view.RegexEditText;
import com.company.forturetelling.view.SettingBar;
import com.company.forturetelling.view.calendar.ChineseCalendar;
import com.company.forturetelling.view.calendar.DialogGLC;
import com.company.forturetelling.wxapi.WXEntryActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.common.utils.CommonUtils;
import com.yun.common.utils.GetJsonDataUtil;
import com.yun.common.utils.SharePreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Lovelin on 2020/2/18
 * <p>
 * Describe:
 */
public class RegisterAnimatorActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.bar_setting_birthday)
    SettingBar bar_setting_birthday;
    @BindView(R.id.bar_setting_address)
    SettingBar bar_setting_address;
    @BindView(R.id.bar_setting_sex)
    SettingBar bar_setting_sex;
    @BindView(R.id.register_relative)
    RelativeLayout register_relative;
    @BindView(R.id.et_register_password1)
    PasswordEditText et_register_password1;
    @BindView(R.id.et_register_password2)
    PasswordEditText et_register_password2;
    @BindView(R.id.cv_register_countdown)
    CountdownView cv_register_countdown;
    @BindView(R.id.tv_title_register)
    TextView tv_title_register;
    @BindView(R.id.btn_register_commit)
    Button btn_register_commit;
    @BindView(R.id.et_register_phone)   //手机号码
            RegexEditText et_register_phone;
    @BindView(R.id.et_register_code)   //手机号码
            EditText et_register_code;
    private int StatueSex;
    private String currentBirthday;
    private Thread thread;
    private boolean isLoaded = false;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private String token;
    private ArrayList<CityBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private String province;
    private String city;
    private String password02;
    private String password01;
    private String phoneType = "1";
    //判断地址数据是否获取成功
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        Log.i("addr", "地址数据开始解析");
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    Log.i("addr", "地址数据获取成功");
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Log.i("addr", "地址数据获取失败");
                    break;


            }
        }
    };
    private String title;
    private String type;

    private void initJsonData() {//解析数据
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String CityData = new GetJsonDataUtil().getJson(RegisterAnimatorActivity.this, "city.json");//获取assets目录下的json文件数据
        ArrayList<CityBean> jsonBean = parseData(CityData);//用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int c = 0; c < jsonBean.get(i).getCity_list().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCity_list().get(c);
                CityList.add(CityName);//添加城市
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    public ArrayList<CityBean> parseData(String result) {//Gson 解析
        ArrayList<CityBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                CityBean entity = gson.fromJson(data.optJSONObject(i).toString(), CityBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_register_anim;
    }

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        setTitleBarVisibility(View.VISIBLE);
        setTitleLeftBtnVisibility(View.VISIBLE);
        setTitleRightBtnVisibility(View.GONE);
        setTitleName("");
        setPageStateView();
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        register = false;
        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        Log.e("Net", "response===校验手机输入分类  1 注册 2 忘记密码=====title===" + title);

        if (title.equals("注册")) {
//            分类 1 注册 2 忘记密码
            phoneType = "1";
            SharePreferenceUtil.put(RegisterAnimatorActivity.this, Constants.WX_Openid, "");   //第一次注册,微信的open必须把它置空

            tv_title_register.setText("注册");
            btn_register_commit.setText("注册");
        } else {
            phoneType = "1";
            tv_title_register.setText("完善信息");
            btn_register_commit.setText("完成");
        }
    }


    @OnClick({R.id.bar_setting_birthday, R.id.bar_setting_address, R.id.bar_setting_sex,
            R.id.btn_register_commit, R.id.cv_register_countdown})
    public void multipleOnclick(View view) {
        switch (view.getId()) {
            case R.id.bar_setting_birthday: //生日
                showInDialog();
                break;
            case R.id.bar_setting_address:  //地址
                ShowPickerView();
                break;
            case R.id.bar_setting_sex: //性别   性别 0 女 1男
                showUpPop(register_relative);
                break;
            case R.id.cv_register_countdown: //倒计时,校验手机是否已经注册过了
                checkPhoneIsRegister();

//                CommonUtils.closeKeyboard(RegisterAnimatorActivity.this);
//                checkData();
                break;
            case R.id.btn_register_commit: //提交
                CommonUtils.closeKeyboard(RegisterAnimatorActivity.this);
                checkData();
                break;

        }
    }

    private boolean register = false;

    private void checkPhoneIsRegister() {
        Log.e("Net", "response===校验手机输入分类  1 注册 2 忘记密码=====phoneType===" + phoneType);
        phone = et_register_phone.getText().toString().trim();
        //todo 此处需要校验手机验证码
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
        } else {
            OkHttpUtils.get()
                    .url(HttpConstants.Register_CheckPhone)
                    .addParams("PhoneNumbers", phone + "") //	手机号
                    .addParams("type", phoneType)   //分类 1 注册 2 忘记密码
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            showToast("请求错误");
                            showError();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Type type = new TypeToken<CheckPhoneBean>() {
                            }.getType();
                            CheckPhoneBean bean = mGson.fromJson(response, type);
//                            0 成功 -1失败
                            Log.e("Net", "response===bean===  getStatus    0 成功 -1失败=====" + bean.getStatus());
                            Log.e("Net", "response===response=== ====" + response);

                            if ("0".equals(bean.getStatus())) {
                                register = true;  //是否能请求注册或者完善信息的开关   ,默认是关了的
                                showToast(bean.getMsg() + "");
                                SharePreferenceUtil.put(RegisterAnimatorActivity.this, Constants.MessageId, bean.getData().getBizId() + "");
                            } else if ("-1".equals(bean.getStatus())) {
                                register = false;
//                                showToast(bean.getMsg() + "");
                                Toast.makeText(RegisterAnimatorActivity.this, "您的手机号已注册", Toast.LENGTH_SHORT).show();
                                showToast(bean.getMsg() + "");
                            }
                        }
                    });
        }

    }

    String requestTypeData = "3";

    private String userid;
    private RegisterBean bean;
    private String phone;
    private String code;

    private void checkData() {
//        if (register) {
        Log.e("Net", "response===时间戳=====secondTimestamp=1==");
        phone = et_register_phone.getText().toString().trim();
        code = et_register_code.getText().toString().trim();
        password01 = et_register_password1.getText().toString().trim() + "";
        password02 = et_register_password2.getText().toString().trim() + "";
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
        } else if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
        } else if (TextUtils.isEmpty(password01)) {
            showToast("密码不能为空");
        } else if (TextUtils.isEmpty(password02)) {
            showToast("确认密码不能为空");
        } else if (TextUtils.isEmpty(currentBirthday)) {
            showToast("出生年月不能为空");
        } else if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city)) {
            showToast("地址不能未空");
        } else if (TextUtils.isEmpty(StatueSex + "")) {
            showToast("性别不能未空");
        } else if (!("".equals(password01)) && !("".equals(password02))) {
            if (!(password01.equals(password02))) {
                showToast("两次输入的密码必须相同");

            } else {
                Log.e("Net", "response===时间戳=====secondTimestamp=2==");

                showLoading();
//            开始请求注册
                String VXopenid = (String) SharePreferenceUtil.get(RegisterAnimatorActivity.this, Constants.WX_Openid, "");
                String messageId = (String) SharePreferenceUtil.get(RegisterAnimatorActivity.this, Constants.MessageId, "");
                String secondTimestamp = getSecondTimestampTwo(new Date()) + "";
                Log.e("Net", "response==username=1==" + phone + "==" + messageId + "==" + code + "==" + secondTimestamp
                        + "==" + StatueSex + "==" + currentBirthday + "==" + province + "==" + city);
                Log.e("Net", "response===时间戳=====secondTimestamp=1==" + secondTimestamp);
                Log.e("Net", "response===时间戳=====secondTimestamp=1==" + System.currentTimeMillis());
                String textTitle = tv_title_register.getText().toString().trim() + "";
                if ("注册".equals(textTitle)) {   //新的账号注册   type=3
                    requestTypeData = "3";
                } else {                              //微信登入   完善   type=1
                    requestTypeData = "1";
                }

                Log.e("Net", "response===时间戳=====requestTypeData=1==" + requestTypeData);

                OkHttpUtils.post()
                        .url(HttpConstants.Register)
                        .addParams("type", requestTypeData)
                        .addParams("PhoneNumbers", phone)
                        .addParams("BizId", "1234")
                        .addParams("vcode", "1234")      //验证码
//                        .addParams("BizId", messageId)
//                        .addParams("vcode", code)      //验证码9
                        .addParams("ytime", secondTimestamp + "")   //验证码时间 传时间戳（注意是秒）
                        .addParams("openid", VXopenid)  //微信唯一标识    可传可不传
                        .addParams("password", password01)
                        .addParams("gender", StatueSex + "")
                        .addParams("birthday", currentBirthday)
                        .addParams("province", province)
                        .addParams("city", city)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                showToast("请求错误");
                                showError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Type type = new TypeToken<RegisterBean>() {
                                }.getType();
                                bean = mGson.fromJson(response, type);
                                Log.e("Net", "login==RegisterBean===" + response);
                                if ("0".equals(bean.getStatus())) {
                                    //sp存token
                                    showContent();
                                    userid = bean.getData().getUserid() + "";
                                    SharePreferenceUtil.put(RegisterAnimatorActivity.this, Constants.USERID, userid + "");
                                    SharePreferenceUtil.put(RegisterAnimatorActivity.this, Constants.Device, "android");
                                    SharePreferenceUtil.put(RegisterAnimatorActivity.this, Constants.Is_Logined, true);
                                    Log.e("Net", "login==RegisterBean==进来了吗111111111111111=");
                                    String perfect = (String) SharePreferenceUtil.get(RegisterAnimatorActivity.this, com.company.forturetelling.global.Constants.WX_Perfect, "false");
                                    Log.e("Wetchat", "login==end===数据保持后台完毕==----注册界面-perfec11t===之前======" + perfect + "");
                                    SharePreferenceUtil.put(RegisterAnimatorActivity.this, Constants.WX_Perfect, "true");
                                    String perfec11t = (String) SharePreferenceUtil.get(RegisterAnimatorActivity.this, com.company.forturetelling.global.Constants.WX_Perfect, "false");
                                    Log.e("Wetchat", "login==end===数据保持后台完毕==----注册界面-perfec11t===之后======" + perfec11t + "");

                                    if ("02".equals(type)) {
                                        EventBus.getDefault().post(new ExitEvent("02"));

                                    } else {
                                        EventBus.getDefault().post(new ExitEvent("登入"));

                                    }
                                    showToast("注册成功");
                                    openActivity(MainActivity.class);
                                    finish();
                                } else {
                                    showContent();
                                    showToast(bean.getMsg() + "");

                                }
                            }
                        });

            }
        }
//        } else {
//            showToast("手机号已被注册");
//        }


    }

    /**
     * 获取精确到秒的时间戳
     *
     * @param date
     * @return
     */
    public static int getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    private PopupWindow popupWindow;
    private TranslateAnimation animation;
    private View popupView;
    private TextView icon_sex_man;
    private TextView icon_sex_wonman;

    /**
     * 修改性别
     *
     * @param
     * @param info_setting_linear
     */
    public void showUpPop(RelativeLayout info_setting_linear) {
        if (popupWindow == null) {
            popupView = View.inflate(this, R.layout.popup_sex_select, null);
            // 参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //隐藏遮罩
                    setBGAlpha(1);


                }
            });
            // 设置背景图片， 必须设置，不然动画没作用
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);
            // 设置点击popupwindow外屏幕其它地方消失
            popupWindow.setOutsideTouchable(true);
            // 平移动画相对于手机屏幕的底部开始，X轴不变，Y轴从1变0
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(200);
        }

        //设置按钮点击监听
        popupView.findViewById(R.id.sex_man).setOnClickListener(this);
        popupView.findViewById(R.id.sex_wonman).setOnClickListener(this);
        popupView.findViewById(R.id.cancel).setOnClickListener(this);
        icon_sex_man = popupView.findViewById(R.id.icon_sex_man);
        icon_sex_wonman = popupView.findViewById(R.id.icon_sex_wonman);
        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(info_setting_linear, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
        //显示遮罩
        setBGAlpha(0.5f);
    }

    public void setBGAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        this.getWindow().setAttributes(lp);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    private void ShowPickerView() {// 弹出地址选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(RegisterAnimatorActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                province = options1Items.get(options1).getPickerViewText();
                city = options2Items.get(options1).get(options2);

                bar_setting_address.setRightText(province + "省 " + city + "市");
//                        + options3Items.get(options1).get(options2).get(options3);
//                mPresenter.sendAddressRequest(province, city, bar_setting_address);
            }
        })
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        pvOptions.setPicker(options1Items, options2Items);//二级选择器（市区）
//pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private DialogGLC mDialog;

    //我的生日
    private void showInDialog() {
        if (mDialog == null) {
            mDialog = new DialogGLC(this, new DialogGLC.onCilckListener() {
                @Override
                public void onGetDataResult(ChineseCalendar calendar) {
                    String data = calendar.get(Calendar.YEAR) + "年"
                            + (calendar.get(Calendar.MONTH) + 1) + "月"
                            + calendar.get(Calendar.DAY_OF_MONTH) + "日"
                            + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                            + calendar.get(Calendar.MINUTE) + "分";
                    currentBirthday = calendar.get(Calendar.YEAR) + "-"
                            + (calendar.get(Calendar.MONTH) + 1) + "-"
                            + calendar.get(Calendar.DAY_OF_MONTH);
                    int intyear = calendar.get(Calendar.YEAR);
                    if (intyear > 2019) {
                        showToast("年份超出范围");
                        bar_setting_birthday.setRightText("");
                        return;
                    }
                    bar_setting_birthday.setRightText("" + currentBirthday);
                    String showToast = "Gregorian : " + calendar.get(Calendar.YEAR) + "年"
                            + (calendar.get(Calendar.MONTH) + 1) + "月"
                            + calendar.get(Calendar.DAY_OF_MONTH) + "日"
                            +
                            "\n"
                            + "Lunar     : " + calendar.getChinese(ChineseCalendar.CHINESE_YEAR)
                            + (calendar.getChinese(ChineseCalendar.CHINESE_MONTH))
                            + calendar.getChinese(ChineseCalendar.CHINESE_DATE)
                            + calendar.getChinese(ChineseCalendar.CHINESE_HOUR)
                            +
                            "\n" + "\n" + "\n" + "-----------上传的日期---------" +
                            calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                            + calendar.get(Calendar.DAY_OF_MONTH) + "-"
                            + calendar.get(Calendar.HOUR_OF_DAY) + "-"
                            + calendar.get(Calendar.MINUTE);
                    mDialog.dismiss();

                }
            });
        }
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        } else {
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
            mDialog.initCalendar();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sex_man: //男  性别 0 女 1男
                icon_sex_man.setVisibility(View.VISIBLE);
                icon_sex_wonman.setVisibility(View.GONE);
                popupWindow.dismiss();
                StatueSex = 1;
                bar_setting_sex.setRightText("" + "男");

                break;
            case R.id.sex_wonman: //女
                icon_sex_man.setVisibility(View.GONE);
                icon_sex_wonman.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                StatueSex = 0;
                bar_setting_sex.setRightText("" + "女");
                break;
            case R.id.cancel:
                popupWindow.dismiss();
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExitEvent(WeChartEvent WeChartEvent) {
        Log.e("Wetchat", "login==end===数据保持后台完毕=register==");

        this.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

    }
}
