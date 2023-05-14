package com.github.militch.jcryptobot.model;

import com.github.militch.jcryptobot.binance.BinanceClient;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class MarketKLine extends BaseModel {
    /**
     * 交易所
     */
    private String exchange;
    /**
     * 交易对符号
     */
    private String symbol;
    /**
     * 小数精度
     */
    private Integer decimals;
    /**
     * 开盘时间
     */
    private LocalDateTime openTime;
    /**
     * 收盘时间
     */
    private LocalDateTime closeTime;
    /**
     * 开盘价
     */
    private BigInteger openPrice;
    /**
     * 收盘价
     */
    private BigInteger closePrice;
    /**
     * 最高价
     */
    private BigInteger highPrice;
    /**
     * 最低价
     */
    private BigInteger lowPrice;
    /**
     * 成交量
     */
    private BigInteger volume;
    /**
     * 成交额
     */
    private BigInteger quoteVolume;
    /**
     * 成交笔数
     */
    private Long count;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalDateTime openTime) {
        this.openTime = openTime;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public BigInteger getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigInteger openPrice) {
        this.openPrice = openPrice;
    }

    public BigInteger getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigInteger closePrice) {
        this.closePrice = closePrice;
    }

    public BigInteger getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigInteger highPrice) {
        this.highPrice = highPrice;
    }

    public BigInteger getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigInteger lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigInteger getVolume() {
        return volume;
    }

    public void setVolume(BigInteger volume) {
        this.volume = volume;
    }

    public BigInteger getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(BigInteger quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }


}
