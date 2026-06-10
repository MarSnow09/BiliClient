package com.RobinNotBad.BiliClient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RobinNotBad.BiliClient.BiliTerminal;
import com.RobinNotBad.BiliClient.R;
import com.RobinNotBad.BiliClient.model.Timeline;
import com.RobinNotBad.BiliClient.util.GlideUtil;
import com.RobinNotBad.BiliClient.util.ToolsUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.DayViewHolder> {
    private final Context context;
    private final List<Timeline.DayInfo> dayInfoList;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public TimelineAdapter(Context context, List<Timeline.DayInfo> dayInfoList) {
        this.context = context;
        this.dayInfoList = dayInfoList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_timeline_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Timeline.DayInfo dayInfo = dayInfoList.get(position);
        holder.dateText.setText(dayInfo.date + (dayInfo.is_today == 1 ? " (今天)" : ""));
        holder.episodesLayout.removeAllViews();
        if (dayInfo.episodes == null || dayInfo.episodes.isEmpty()) return;

        for (Timeline.Episode episode : dayInfo.episodes) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.cell_timeline_episode, holder.episodesLayout, false);
            ImageView cover = itemView.findViewById(R.id.img_cover);
            TextView title = itemView.findViewById(R.id.text_title);
            TextView episodeText = itemView.findViewById(R.id.text_episode);
            TextView time = itemView.findViewById(R.id.text_time);

            title.setText(episode.title);
            episodeText.setText(episode.pub_index);
            if (episode.pub_ts > 0) {
                time.setText(timeFormat.format(new Date(episode.pub_ts * 1000)));
            } else {
                time.setText(episode.pub_time);
            }

            Glide.with(BiliTerminal.context).asDrawable()
                    .load(GlideUtil.url(episode.cover))
                    .placeholder(R.mipmap.placeholder)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(ToolsUtil.dp2px(8))).sizeMultiplier(0.85f))
                    .into(cover);
            holder.episodesLayout.addView(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return dayInfoList != null ? dayInfoList.size() : 0;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateText;
        public final LinearLayout episodesLayout;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.text_date);
            episodesLayout = itemView.findViewById(R.id.episodes_layout);
        }
    }
}
