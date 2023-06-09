package com.github.militch.jcryptobot.binance;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BinanceClient {

    private static final Logger logger = LogManager.getLogger();
    private final String apiEndpoint;
    private final String apiKey;
    private final String secretKey;

    public static final String DEFAULT_API_ENDPOINT = "https://api.binance.com";
    private final OkHttpClient client = new OkHttpClient();
    private final static Gson g = new Gson();

    public BinanceClient(String apiKey, String secretKey){
        this(apiKey, secretKey, DEFAULT_API_ENDPOINT);
    }

    public BinanceClient(String apiKey, String secretKey, String apiEndpoint) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.apiEndpoint = apiEndpoint;
    }


    public boolean ping(){
        String data = doRequestGetJsonString("/api/v3/ping", null);
        return data != null && data.equals("{}");
    }
    public long getTime() {
        Map<String, String> data = doReuqestGetStringMap("/api/v3/time");
        if (data == null) { return 0; }
        String serverTime = data.get("serverTime");
        return Long.parseLong(serverTime);
    }

    public Map<String, String> doReuqestGetStringMap(String path){
        String data = doRequestGetJsonString(path, null);
        if (data == null){ return null; }
        return g.fromJson(data, new TypeToken<Map<String, String>>(){}.getType());
    }

    public KLine getLatestKLine(String symbol, String interval) {
        Map<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("interval", interval);
        //params.put("startTime", "");
        //params.put("endTime", "");
        params.put("limit", "1");
        List<KLine> kLines = getKLines(params);
        if (kLines == null || kLines.isEmpty())
            return null;
        return kLines.stream().findFirst().get();
    }
    public List<KLine> getKLines(Map<String, String> params){
        String dataString = doRequestGetJsonString("/api/v3/klines", params);
        List<String[]> data = g.fromJson(dataString, new TypeToken<List<String[]>>(){}.getType());
        if (data == null || data.isEmpty())
            return null;
        return data.stream().filter((s)-> s.length >= 11).map((s)->{
            KLine kLine = new KLine();
            Long openTime = Long.parseLong(s[0]);
            kLine.setOpenTime(openTime);
            kLine.setOpenPrice(s[1]);
            kLine.setHighPrice(s[2]);
            kLine.setLowPrice(s[3]);
            kLine.setClosePrice(s[4]);
            kLine.setVolume(s[5]);
            Long closeTime = Long.parseLong(s[6]);
            kLine.setCloseTime(closeTime);
            kLine.setQuoteVolume(s[7]);
            Long count = Long.parseLong(s[8]);
            kLine.setCount(count);
            return kLine;
        }).collect(Collectors.toList());
    }
    public String doRequestGetJsonString(String path, Map<String, String> params) {
        String url = apiEndpoint + path;
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null)
            return null;
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        urlBuilder.fragment(url);
        if (params != null && !params.isEmpty()) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                ResponseBody rb = response.body();
                String body = null;
                if (rb != null){
                    body = rb.string();
                }
                logger.warn("Failed request: status({}) body: {}", response.code(), body);
                return null;
            }
            ResponseBody rb = response.body();
            if (rb == null) {
                return null;
            }
            return rb.string();
        }catch (IOException e) {
            return null;
        }
    }

}
