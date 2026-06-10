package com.RobinNotBad.BiliClient.activity.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.RobinNotBad.BiliClient.R;
import com.RobinNotBad.BiliClient.activity.base.BaseActivity;
import com.RobinNotBad.BiliClient.api.UserInfoApi;
import com.RobinNotBad.BiliClient.util.CenterThreadPool;
import com.RobinNotBad.BiliClient.util.MsgUtil;
import com.RobinNotBad.BiliClient.util.SharedPreferencesUtil;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

public class EditSignActivity extends BaseActivity {
    private TextView charCount;
    private EditText editText;
    private MaterialCardView submit;
    private boolean isSubmitting = false;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sign);
        if (SharedPreferencesUtil.getLong(SharedPreferencesUtil.mid, 0L) == 0) {
            MsgUtil.showMsg("还没有登录喵~");
            finish();
            return;
        }

        String currentSign = getIntent().getStringExtra("currentSign");
        if (currentSign == null) currentSign = "";

        editText = findViewById(R.id.editText);
        charCount = findViewById(R.id.charCount);
        submit = findViewById(R.id.submit);

        editText.setText(currentSign);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(70)});
        editText.setSelection(editText.getText().length());
        updateCharCount(editText.getText().toString().length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCharCount(s.length());
            }
        });
        submit.setOnClickListener(this::submitSign);
    }

    private void submitSign(View view) {
        if (isSubmitting) {
            MsgUtil.showMsg("正在提交中...");
            return;
        }
        if (!SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.cookie_refresh, true)) {
            MsgUtil.showDialog("无法提交", "上一次的Cookie刷新失败了，\n您可能需要重新登录以进行敏感操作", -1);
            return;
        }

        String sign = editText.getText().toString();
        isSubmitting = true;
        submit.setEnabled(false);
        CenterThreadPool.run(() -> {
            try {
                JSONObject result = UserInfoApi.updateUserSign(sign);
                int code = result.getInt("code");
                String message = result.optString("message", "");
                if (isDestroyed()) return;
                runOnUiThread(() -> handleSubmitResult(code, message));
            } catch (Exception e) {
                if (isDestroyed()) return;
                runOnUiThread(() -> {
                    isSubmitting = false;
                    submit.setEnabled(true);
                    MsgUtil.err("修改个人描述失败", e);
                });
            }
        });
    }

    private void handleSubmitResult(int code, String message) {
        isSubmitting = false;
        submit.setEnabled(true);
        if (code == 0) {
            MsgUtil.showMsg("修改成功，等待审核");
            setResult(RESULT_OK);
            finish();
            return;
        }

        String text = message;
        if (code == -101) {
            text = "账号未登录";
        } else if (code == -111) {
            text = "CSRF校验失败";
        } else if (code == 40015) {
            text = "签名包含敏感词";
        } else if (code == 40021) {
            text = "签名不能包含表情图片";
        } else if (code == 40022) {
            text = "签名过长";
        } else if (text.isEmpty()) {
            text = "修改失败";
        }
        MsgUtil.showMsg(text);
    }

    private void updateCharCount(int count) {
        charCount.setText(count + "/70");
    }
}
