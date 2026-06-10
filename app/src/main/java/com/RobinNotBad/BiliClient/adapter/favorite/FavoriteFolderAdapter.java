package com.RobinNotBad.BiliClient.adapter.favorite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RobinNotBad.BiliClient.BiliTerminal;
import com.RobinNotBad.BiliClient.R;
import com.RobinNotBad.BiliClient.activity.user.favorite.FavoriteVideoListActivity;
import com.RobinNotBad.BiliClient.activity.user.favorite.FavouriteOpusListActivity;
import com.RobinNotBad.BiliClient.model.FavoriteFolder;
import com.RobinNotBad.BiliClient.util.GlideUtil;
import com.RobinNotBad.BiliClient.util.MsgUtil;
import com.RobinNotBad.BiliClient.util.StringUtil;
import com.RobinNotBad.BiliClient.util.ToolsUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

//收藏夹Adapter

public class FavoriteFolderAdapter extends RecyclerView.Adapter<FavoriteFolderAdapter.FavoriteHolder> {
    private static final int TYPE_CREATE = 0;
    private static final int TYPE_FOLDER = 1;
    private static final int TYPE_OPUS = 2;

    final Context context;
    final ArrayList<FavoriteFolder> folderList;
    final long mid;
    private OnCreateClickListener onCreateClickListener;
    private OnLongClickListener onLongClickListener;

    public FavoriteFolderAdapter(Context context, ArrayList<FavoriteFolder> folderList, long mid) {
        this.context = context;
        this.folderList = folderList;
        this.mid = mid;
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CREATE) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.cell_create_folder_button, parent, false);
            return new FavoriteHolder(view, true);
        }
        View view = LayoutInflater.from(this.context).inflate(R.layout.cell_favorite_folder_list, parent, false);
        return new FavoriteHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        if (folderList == null)
            return;
        if (getItemViewType(position) == TYPE_CREATE) {
            holder.itemView.setOnClickListener(view -> {
                if (onCreateClickListener != null) onCreateClickListener.onCreateClick();
            });
        } else if (position == folderList.size() + 1) {
            holder.name.setText("图文收藏夹");
            holder.count.setText("");
            Glide.with(BiliTerminal.context).asDrawable()
                    .load(StringUtil.getDrawable(context, R.drawable.article_fav_cover))
                    .transition(GlideUtil.getTransitionOptions())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(ToolsUtil.dp2px(5))))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.cover);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FavouriteOpusListActivity.class);
                context.startActivity(intent);
            });
            holder.itemView.setOnLongClickListener(null);
        } else if (position > 0 && position <= folderList.size()) {
            FavoriteFolder folder = folderList.get(position - 1);
            if (folder == null)
                return;

            holder.name.setText(StringUtil.htmlToString(folder.name));
            holder.count.setText(folder.videoCount + "/" + folder.maxCount);
            Glide.with(BiliTerminal.context).asDrawable().load(GlideUtil.url(folder.cover))
                    .transition(GlideUtil.getTransitionOptions())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(ToolsUtil.dp2px(5))))
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.cover);
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setClass(context, FavoriteVideoListActivity.class);
                intent.putExtra("fid", folder.id);
                intent.putExtra("mid", mid);
                intent.putExtra("name", folder.name);
                context.startActivity(intent);
            });
            holder.itemView.setOnLongClickListener(view -> {
                if (folder.isDefault) {
                    MsgUtil.showMsg("默认收藏夹不能编辑");
                    return true;
                }
                if (onLongClickListener != null) onLongClickListener.onLongClick(position - 1);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return folderList != null ? folderList.size() + 2 : 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_CREATE;
        return position == folderList.size() + 1 ? TYPE_OPUS : TYPE_FOLDER;
    }

    public void setOnCreateClickListener(OnCreateClickListener listener) {
        onCreateClickListener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        onLongClickListener = listener;
    }

    public static class FavoriteHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView count;
        ImageView cover;

        public FavoriteHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_title);
            count = itemView.findViewById(R.id.text_itemcount);
            cover = itemView.findViewById(R.id.img_cover);
        }

        public FavoriteHolder(@NonNull View itemView, boolean createButton) {
            super(itemView);
        }
    }

    public interface OnCreateClickListener {
        void onCreateClick();
    }

    public interface OnLongClickListener {
        void onLongClick(int position);
    }
}
