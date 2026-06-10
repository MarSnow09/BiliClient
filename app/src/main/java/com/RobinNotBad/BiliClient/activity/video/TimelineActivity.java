package com.RobinNotBad.BiliClient.activity.video;

import android.os.Bundle;
import android.view.View;

import com.RobinNotBad.BiliClient.activity.base.RefreshMainActivity;
import com.RobinNotBad.BiliClient.adapter.TimelineAdapter;
import com.RobinNotBad.BiliClient.api.TimelineApi;
import com.RobinNotBad.BiliClient.model.Timeline;
import com.RobinNotBad.BiliClient.util.CenterThreadPool;
import com.RobinNotBad.BiliClient.util.MsgUtil;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends RefreshMainActivity {
    private TimelineAdapter adapter;
    private List<Timeline.DayInfo> dayInfoList;
    private final String types = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMenuClick();
        setPageName("时间线");
        dayInfoList = new ArrayList<>();
        setOnRefreshListener(() -> {
            dayInfoList.clear();
            loadTimeline();
        });
        loadTimeline();
    }

    private void loadTimeline() {
        setRefreshing(true);
        CenterThreadPool.run(() -> {
            try {
                List<Timeline.DayInfo> list = TimelineApi.getTimeline(types, 7, 7);
                runOnUiThread(() -> {
                    dayInfoList.addAll(list);
                    if (adapter == null) {
                        adapter = new TimelineAdapter(this, dayInfoList);
                        setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    setRefreshing(false);
                    recyclerView.setVisibility(dayInfoList.isEmpty() ? View.GONE : View.VISIBLE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setRefreshing(false);
                    report(e);
                    MsgUtil.showMsgLong("加载失败");
                });
            }
        });
    }
}
