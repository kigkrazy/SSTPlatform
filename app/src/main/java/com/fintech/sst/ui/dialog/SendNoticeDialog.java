package com.fintech.sst.ui.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.fintech.sst.R;
import com.fintech.sst.data.db.Notice;
import com.fintech.sst.helper.ExpansionKt;

import java.text.DecimalFormat;
import java.util.Calendar;


public class SendNoticeDialog extends Dialog {
    private ClickListener clickListener;
    private Notice notice = new Notice();
    public SendNoticeDialog(@NonNull Context context, ClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
    }

    public SendNoticeDialog(@NonNull Context context, int themeResId, ClickListener clickListener) {
        super(context, themeResId);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
    }

    protected SendNoticeDialog(@NonNull Context context , boolean cancelable, ClickListener clickListener, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
        setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_send_notice);

        final RadioButton rb_ali = findViewById(R.id.rb_ali);
        final RadioButton rb_wechat = findViewById(R.id.rb_wechat);
        final RadioButton rb_bank = findViewById(R.id.rb_bank);
        final RadioButton rb_yun = findViewById(R.id.rb_yun);
        final EditText etAccount = findViewById(R.id.et_amount);
        final EditText etTime = findViewById(R.id.et_time);
        final EditText etMark = findViewById(R.id.et_mark);
        final Button btnTime = findViewById(R.id.btnTime);
        final Button btnY = findViewById(R.id.btnY);
        Button btnN = findViewById(R.id.btnN);
        btnY.setEnabled(false);

        rb_ali.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    notice.title = "支付宝通知";
                    notice.packageName = "com.eg.android.AlipayGphone";
                    notice.type = 2001;
                    if (TextUtils.isEmpty(etAccount.getText())) return;
                    notice.amount = getAmount(etAccount.getText().toString());
                    notice.content = "支付宝通知：xxx付款"+ notice.amount +"元";
                }
            }
        });

        rb_wechat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    notice.title = "微信支付";
                    notice.packageName = "com.tencent.mm";
                    notice.type = 1001;
                    if (TextUtils.isEmpty(etAccount.getText())) return;
                    notice.amount = getAmount(etAccount.getText().toString());
                    notice.content = "微信支付: 微信支付收款"+ notice.amount +"元";
                    notice.mark = etMark.getText().toString();
                }
            }
        });
        rb_bank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    notice.title = "银行支付";
                    notice.packageName = "";
                    notice.type = 3001;
                    if (TextUtils.isEmpty(etAccount.getText())) return;
                    notice.amount = getAmount(etAccount.getText().toString());
                    notice.content = "银行收款"+ notice.amount +"元";
                    notice.mark = etMark.getText().toString();
                }
            }
        });
        rb_yun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    notice.title = "云闪付支付";
                    notice.packageName = "com.unionpay";
                    notice.type = 4001;
                    if (TextUtils.isEmpty(etAccount.getText())) return;
                    notice.amount = getAmount(etAccount.getText().toString());
                    notice.content = "云闪付收款"+ notice.amount +"元";
                    notice.mark = etMark.getText().toString();
                }
            }
        });

        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notice.amount = getAmount(s.toString());
                if (rb_ali.isChecked())
                    notice.content = "支付宝通知：xxx付款"+ notice.amount +"元";

                if (rb_wechat.isChecked())
                    notice.content = "微信支付: 微信支付收款"+ notice.amount +"元";

                btnY.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etMark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notice.mark = s.toString();

                btnY.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR,hourOfDay);
                        cal.set(Calendar.MINUTE,minute);

                        notice.saveTime = cal.getTimeInMillis();
                        etTime.setText(ExpansionKt.getTime(notice.saveTime));
                    }
                },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),true);
                dialog.show();
            }
        });

        btnY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onSure(notice);
                dismiss();
            }
        });

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int windowWidth = outMetrics.widthPixels;
        int windowHeight = outMetrics.heightPixels;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (windowWidth * 0.95); // 宽度设置为屏幕的一定比例大小
//        if (heightScale == 0) {
//            params.gravity = Gravity.CENTER;
//        } else {
//            params.gravity = Gravity.TOP;
//            params.y = (int) (windowHeight * heightScale); // 距离顶端高度设置为屏幕的一定比例大小
//        }
        getWindow().setAttributes(params);

    }

    public interface ClickListener{
        void onSure(Notice notice);
    }

    private String getAmount(String amount){
        try {
            DecimalFormat format = new DecimalFormat("0.00");
            return format.format(Float.parseFloat(amount));
        }catch (Exception e){
            e.printStackTrace();
            return "0.00";
        }
    }
}
