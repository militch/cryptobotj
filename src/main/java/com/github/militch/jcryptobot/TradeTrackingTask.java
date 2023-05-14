package com.github.militch.jcryptobot;

import com.github.militch.jcryptobot.binance.BinanceClient;
import com.github.militch.jcryptobot.binance.KLine;
import com.github.militch.jcryptobot.core.TradingSymbol;
import com.github.militch.jcryptobot.mapper.MarketKLineMapper;
import com.github.militch.jcryptobot.model.MarketKLine;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TradeTrackingTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger();
    private final BinanceClient client;
    private final SqlSessionFactory sqlSessionFactory;
    public TradeTrackingTask(BinanceClient client, SqlSessionFactory sqlSessionFactory){
        this.client = client;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    private final Map<Long, KLine> kLines = new LinkedHashMap<>();
    private long lastTime;
    private int fetch(long start){
        Map<String, String> params = new HashMap<>();
        params.put("symbol", TradingSymbol.BTCUSDT.getSymbol());
        params.put("interval", "1m");
        params.put("limit", "200");
        params.put("startTime", String.valueOf(start));
        List<KLine> gotList = client.getKLines(params);
        if (gotList == null || gotList.isEmpty()) {
            return 0;
        }
        int n = 0;
        for (KLine kl : gotList) {
            if (gotList.size() < 200) {
                if (n == gotList.size() - 1) {
                    break;
                }
            }
            n++;
            kLines.put(kl.getOpenTime(), kl);
            lastTime = kl.getCloseTime();
            insertKLine(kl, TradingSymbol.BTCUSDT);
        }
        return n;
    }
    private void req(long start){
        while (true) {
            int n = fetch(start);
            start += ONE_MINUTE * n;
            if (n < 200) {
                break;
            }
        }
    }
    private static final long ONE_MINUTE = 1000 * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;
    private static final long ONE_YEAR = ONE_DAY * 365;

    private final AtomicInteger ai = new AtomicInteger(0);

    private static LocalDateTime timestampToLocalDateTime(Long t){
        if (t == null) return null;
        Instant i = Instant.ofEpochMilli(t);
        return LocalDateTime.ofInstant(i, ZoneId.systemDefault());
    }

    private static long localDateTimeToTimestamp(LocalDateTime dateTime){
        return dateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }
    private static long currentTimestamp(){
        LocalDateTime now = LocalDateTime.now();
        now = now.withNano(0).withSecond(0);
        return localDateTimeToTimestamp(now);
    }
    private final ExecutorService es = Executors.newFixedThreadPool(6);
    private void doSync() {
        if (!ai.compareAndSet(0, 1)){
            return;
        }
        if (lastTime == 0) {
            long now = currentTimestamp();
            lastTime = now - ONE_HOUR;
        }
        //logger.info("fetch start: {}({})", timestampToLocalDateTime(lastTime), lastTime);
        req(lastTime);
        ai.set(0);
    }
    private static BigInteger scalePrice(String price, int decimals){
        if (price == null) return null;
        BigDecimal priceObj = new BigDecimal(price);
        BigDecimal cover = BigDecimal.TEN.pow(decimals);
        priceObj = priceObj.multiply(cover);
        return priceObj.toBigInteger();
    }
    private void insertKLine(KLine kLine, TradingSymbol symbol){
        MarketKLine marketKLine = new MarketKLine();
        marketKLine.setExchange("BINANCE");
        marketKLine.setSymbol(symbol.getSymbol());
        int decimals = symbol.getDecimals();
        marketKLine.setDecimals(decimals);
        marketKLine.setCreateTime(LocalDateTime.now());
        LocalDateTime openTime = timestampToLocalDateTime(kLine.getOpenTime());
        marketKLine.setOpenTime(openTime);
        LocalDateTime closeTime = timestampToLocalDateTime(kLine.getCloseTime());
        marketKLine.setCloseTime(closeTime);
        BigInteger openPrice = scalePrice(kLine.getOpenPrice(), decimals);
        marketKLine.setOpenPrice(openPrice);
        BigInteger closePrice = scalePrice(kLine.getClosePrice(), decimals);
        marketKLine.setClosePrice(closePrice);
        BigInteger highPrice = scalePrice(kLine.getHighPrice(), decimals);
        marketKLine.setHighPrice(highPrice);
        BigInteger lowPrice = scalePrice(kLine.getLowPrice(), decimals);
        marketKLine.setLowPrice(lowPrice);
        BigInteger volume = scalePrice(kLine.getVolume(), decimals);
        marketKLine.setVolume(volume);
        BigInteger quoteVolume = scalePrice(kLine.getQuoteVolume(), decimals);
        marketKLine.setQuoteVolume(quoteVolume);
        marketKLine.setCount(kLine.getCount());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)){
            //sqlSession.insert(sql, marketKLine);
            MarketKLineMapper mapper = sqlSession.getMapper(MarketKLineMapper.class);
            int exists = mapper.countByOpenTime(marketKLine.getOpenTime());
            if (exists == 1) {
                return;
            }
            logger.info("Got: {}", kLine);
            mapper.insert(marketKLine);
        }
    }
    @Override
    public void run() {
        es.execute(this::doSync);
        //sqlSessionFactory.openSession();
        //logger.info("Start sync");
    }
}
