package com.RobinNotBad.BiliClient.api;

import com.RobinNotBad.BiliClient.model.ExpLog;
import com.RobinNotBad.BiliClient.util.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpLogApi {

    public static List<ExpLog> getExpLog() throws IOException, JSONException {
        JSONObject result = NetWorkUtil.getJson("https://api.bilibili.com/x/member/web/exp/log?jsonp=jsonp&web_location=333.33");
        ArrayList<ExpLog> logList = new ArrayList<>();
        if (result.getInt("code") == 0) {
            JSONArray list = result.getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject log = list.getJSONObject(i);
                logList.add(new ExpLog(log.getInt("delta"), log.getString("time"), log.getString("reason")));
            }
        }
        return logList;
    }
}
