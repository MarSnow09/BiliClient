package com.RobinNotBad.BiliClient.api;

import com.RobinNotBad.BiliClient.model.Timeline;
import com.RobinNotBad.BiliClient.util.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimelineApi {

    public static List<Timeline.DayInfo> getTimeline(String types, int before, int after) throws IOException, JSONException {
        JSONObject result = NetWorkUtil.getJson("https://api.bilibili.com/pgc/web/timeline?types=" + types + "&before=" + before + "&after=" + after);
        if (result.getInt("code") != 0) {
            throw new JSONException(result.optString("message", "请求失败"));
        }

        JSONArray resultList = result.getJSONArray("result");
        ArrayList<Timeline.DayInfo> dayInfoList = new ArrayList<>();
        for (int i = 0; i < resultList.length(); i++) {
            JSONObject dayJson = resultList.getJSONObject(i);
            Timeline.DayInfo dayInfo = new Timeline.DayInfo();
            dayInfo.date = dayJson.getString("date");
            dayInfo.date_ts = dayJson.getLong("date_ts");
            dayInfo.day_of_week = dayJson.getInt("day_of_week");
            dayInfo.is_today = dayJson.getInt("is_today");
            dayInfo.episodes = new ArrayList<>();

            JSONArray episodeList = dayJson.getJSONArray("episodes");
            for (int j = 0; j < episodeList.length(); j++) {
                JSONObject episodeJson = episodeList.getJSONObject(j);
                Timeline.Episode episode = new Timeline.Episode();
                episode.cover = episodeJson.optString("cover", "");
                episode.delay = episodeJson.optInt("delay", 0);
                episode.delay_id = episodeJson.optLong("delay_id", 0L);
                episode.delay_index = episodeJson.optString("delay_index", "");
                episode.delay_reason = episodeJson.optString("delay_reason", "");
                episode.ep_cover = episodeJson.optString("ep_cover", "");
                episode.episode_id = episodeJson.optLong("episode_id", 0L);
                episode.pub_index = episodeJson.optString("pub_index", "");
                episode.pub_time = episodeJson.optString("pub_time", "");
                episode.pub_ts = episodeJson.optLong("pub_ts", 0L);
                episode.published = episodeJson.optInt("published", 0);
                episode.follows = episodeJson.optString("follows", "");
                episode.plays = episodeJson.optString("plays", "");
                episode.season_id = episodeJson.optLong("season_id", 0L);
                episode.square_cover = episodeJson.optString("square_cover", "");
                episode.title = episodeJson.optString("title", "");
                dayInfo.episodes.add(episode);
            }
            dayInfoList.add(dayInfo);
        }
        return dayInfoList;
    }
}
