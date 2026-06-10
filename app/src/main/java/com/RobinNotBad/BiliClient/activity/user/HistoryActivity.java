package com.RobinNotBad.BiliClient.activity.user;

import android.os.Bundle;

import com.RobinNotBad.BiliClient.activity.base.RefreshListActivity;
import com.RobinNotBad.BiliClient.adapter.video.VideoCardAdapter;
import com.RobinNotBad.BiliClient.api.HistoryApi;
import com.RobinNotBad.BiliClient.model.ApiResult;
import com.RobinNotBad.BiliClient.model.VideoCard;
import com.RobinNotBad.BiliClient.util.CenterThreadPool;
import com.RobinNotBad.BiliClient.util.MsgUtil;

import java.util.ArrayList;
import java.util.List;

//历史记录
//2023-08-18
//2024-04-30

public class HistoryActivity extends RefreshListActivity {

    private ApiResult lastResult = new ApiResult();
    private ArrayList<VideoCard> videoList;
    private VideoCardAdapter videoCardAdapter;
    private int longClickPosition = -1;
    private long longClickTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageName("历史记录");

        recyclerView.setHasFixedSize(true);

        videoList = new ArrayList<>();

        CenterThreadPool.run(() -> {
            try {
                lastResult = HistoryApi.getHistory(lastResult, videoList);
                if (lastResult.code == 0) {
                    videoCardAdapter = new VideoCardAdapter(this, videoList);
                    videoCardAdapter.setOnLongClickListener(this::deleteHistory);
                    setOnLoadMoreListener(this::continueLoading);
                    setRefreshing(false);
                    setAdapter(videoCardAdapter);

                    if (lastResult.isBottom) {
                        setBottom(true);
                    }
                } else MsgUtil.showMsg(lastResult.message);

            } catch (Exception e) {
                loadFail(e);
            }
        });
    }

    private void deleteHistory(int position) {
        if (position < 0 || position >= videoList.size()) return;

        long timestamp = System.currentTimeMillis();
        if (longClickPosition == position && timestamp - longClickTimestamp < 4000) {
            VideoCard videoCard = videoList.get(position);
            CenterThreadPool.run(() -> {
                try {
                    int result = HistoryApi.deleteHistory(videoCard.aid);
                    longClickPosition = -1;
                    if (result == 0) {
                        runOnUiThread(() -> removeHistoryItem(position, videoCard));
                    } else {
                        runOnUiThread(() -> MsgUtil.showMsg("删除失败，错误码：" + result));
                    }
                } catch (Exception e) {
                    report(e);
                }
            });
        } else {
            longClickPosition = position;
            longClickTimestamp = timestamp;
            MsgUtil.showMsg("再次长按删除");
        }
    }

    private void removeHistoryItem(int position, VideoCard videoCard) {
        int removePosition = -1;
        if (position >= 0 && position < videoList.size() && videoList.get(position) == videoCard) {
            removePosition = position;
        } else {
            removePosition = videoList.indexOf(videoCard);
        }

        if (removePosition == -1) return;

        MsgUtil.showMsg("删除成功");
        videoList.remove(removePosition);
        videoCardAdapter.notifyItemRemoved(removePosition);
        videoCardAdapter.notifyItemRangeChanged(removePosition, videoList.size() - removePosition);
        if (videoList.isEmpty()) showEmptyView();
    }

    private void continueLoading(int page) {
        CenterThreadPool.run(() -> {
            try {
                List<VideoCard> list = new ArrayList<>();
                lastResult = HistoryApi.getHistory(lastResult, list);
                if (lastResult.code == 0) {
                    runOnUiThread(() -> {
                        videoList.addAll(list);
                        videoCardAdapter.notifyItemRangeInserted(videoList.size() - list.size(), list.size());
                    });
                    if (lastResult.isBottom) {
                        setBottom(true);
                    }
                }
                setRefreshing(false);
            } catch (Exception e) {
                loadFail(e);
            }
        });
    }
}
