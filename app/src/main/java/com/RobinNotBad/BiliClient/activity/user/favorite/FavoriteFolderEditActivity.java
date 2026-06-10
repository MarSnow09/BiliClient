package com.RobinNotBad.BiliClient.activity.user.favorite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

import com.RobinNotBad.BiliClient.R;
import com.RobinNotBad.BiliClient.activity.base.BaseActivity;
import com.RobinNotBad.BiliClient.api.FavoriteApi;
import com.RobinNotBad.BiliClient.util.CenterThreadPool;
import com.RobinNotBad.BiliClient.util.MsgUtil;
import com.google.android.material.card.MaterialCardView;

public class FavoriteFolderEditActivity extends BaseActivity {
    private EditText editTitle;
    private EditText editIntro;
    private MaterialCardView btnSave;
    private MaterialCardView btnDelete;
    private long mediaId;
    private String originalTitle;
    private boolean isDefault;
    private int deleteClickCount = 0;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_folder_edit);

        Intent intent = getIntent();
        mediaId = intent.getLongExtra("mediaId", 0L);
        originalTitle = intent.getStringExtra("title");
        String intro = intent.getStringExtra("intro");
        isDefault = intent.getBooleanExtra("isDefault", false);

        editTitle = findViewById(R.id.editTitle);
        editIntro = findViewById(R.id.editIntro);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        if (originalTitle != null) editTitle.setText(originalTitle);
        if (intro != null) editIntro.setText(intro);

        if (isDefault) {
            editTitle.setEnabled(false);
            editIntro.setEnabled(false);
            btnSave.setClickable(false);
            btnSave.setAlpha(0.5f);
            btnDelete.setVisibility(android.view.View.GONE);
            MsgUtil.showMsg("默认收藏夹不能编辑或删除");
            return;
        }

        btnSave.setOnClickListener(view -> saveFolder());
        btnDelete.setOnClickListener(view -> handleDeleteClick());
    }

    private void saveFolder() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            MsgUtil.showMsg("请输入收藏夹名称");
            return;
        }

        String intro = editIntro.getText().toString().trim();
        btnSave.setClickable(false);
        CenterThreadPool.run(() -> {
            try {
                int code = FavoriteApi.editFolder(mediaId, title, intro, 0);
                runOnUiThread(() -> {
                    btnSave.setClickable(true);
                    if (code == 0) {
                        MsgUtil.showMsg("保存成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        MsgUtil.showMsg("保存失败，错误码：" + code);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSave.setClickable(true);
                    report(e);
                });
            }
        });
    }

    private void handleDeleteClick() {
        deleteClickCount++;
        if (deleteClickCount == 1) {
            MsgUtil.showMsg("再次点击删除按钮确认删除");
            new Handler().postDelayed(() -> deleteClickCount = 0, 3000L);
        } else if (deleteClickCount >= 2) {
            deleteClickCount = 0;
            deleteFolder();
        }
    }

    private void deleteFolder() {
        btnDelete.setClickable(false);
        CenterThreadPool.run(() -> {
            try {
                int code = FavoriteApi.deleteFolder(mediaId);
                runOnUiThread(() -> {
                    btnDelete.setClickable(true);
                    if (code == 0) {
                        MsgUtil.showMsg("删除成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        MsgUtil.showMsg("删除失败，错误码：" + code);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnDelete.setClickable(true);
                    report(e);
                });
            }
        });
    }
}
