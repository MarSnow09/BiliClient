package com.RobinNotBad.BiliClient.api;

import com.RobinNotBad.BiliClient.model.VideoCard;
import com.RobinNotBad.BiliClient.util.NetWorkUtil;
import com.RobinNotBad.BiliClient.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaMeta;

public class RankingApi {

    public static void getRanking(List<VideoCard> videoList, int rid, String type) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/web-interface/ranking/v2" + new NetWorkUtil.FormData()
                .setUrlParam(true)
                .put("rid", rid)
                .put(IjkMediaMeta.IJKM_KEY_TYPE, type)
                .put("web_location", "333.934");
        JSONObject result = NetWorkUtil.getJson(ConfInfoApi.signWBI(url));
        if (!result.has("data") || result.isNull("data")) return;

        JSONObject data = result.getJSONObject("data");
        if (!data.has("list") || data.isNull("list")) return;

        JSONArray list = data.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            VideoCard videoCard = new VideoCard();
            videoCard.aid = item.getLong("aid");
            videoCard.bvid = item.getString("bvid");
            videoCard.cover = item.getString("pic");
            videoCard.title = item.getString("title");
            videoCard.upName = item.getJSONObject("owner").getString("name");
            videoCard.view = StringUtil.toWan(item.getJSONObject("stat").getLong("view")) + "观看";
            videoList.add(videoCard);
        }
    }
}
