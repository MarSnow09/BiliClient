package com.RobinNotBad.BiliClient.activity.video;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.RobinNotBad.BiliClient.activity.base.RefreshMainActivity;
import com.RobinNotBad.BiliClient.adapter.video.VideoCardAdapter;
import com.RobinNotBad.BiliClient.api.RankingApi;
import com.RobinNotBad.BiliClient.model.VideoCard;
import com.RobinNotBad.BiliClient.util.CenterThreadPool;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends RefreshMainActivity {
    private VideoCardAdapter videoCardAdapter;
    private ArrayList<VideoCard> videoCardList;
    private boolean firstRefresh = true;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMenuClick();
        setPageName("全站排行榜");
        setOnRefreshListener(this::loadRanking);
        loadRanking();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadRanking() {
        Log.e("debug", "刷新排行榜");
        if (firstRefresh) {
            videoCardList = new ArrayList<>();
        } else {
            int last = videoCardList.size();
            videoCardList.clear();
            videoCardAdapter.notifyItemRangeRemoved(0, last);
        }
        setRefreshing(true);

        CenterThreadPool.run(() -> {
            try {
                List<VideoCard> list = new ArrayList<>();
                RankingApi.getRanking(list, 0, "all");
                runOnUiThread(() -> {
                    videoCardList.addAll(list);
                    setRefreshing(false);
                    if (firstRefresh) {
                        firstRefresh = false;
                        videoCardAdapter = new VideoCardAdapter(this, videoCardList);
                        setAdapter(videoCardAdapter);
                    } else {
                        videoCardAdapter.notifyItemRangeInserted(videoCardList.size() - list.size(), list.size());
                    }
                });
            } catch (Exception e) {
                loadFail(e);
            }
        });
    }
}
