package com.RobinNotBad.BiliClient.api;

import com.RobinNotBad.BiliClient.model.CoinLog;
import com.RobinNotBad.BiliClient.util.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoinLogApi {

    public static List<CoinLog> getCoinLog() throws IOException, JSONException {
        JSONObject result = NetWorkUtil.getJson("https://api.bilibili.com/x/member/web/coin/log");
        ArrayList<CoinLog> logList = new ArrayList<>();
        if (result.getInt("code") == 0) {
            JSONArray list = result.getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject log = list.getJSONObject(i);
                logList.add(new CoinLog(log.getString("time"), log.getInt("delta"), log.getString("reason")));
            }
        }
        return logList;
    }
}
