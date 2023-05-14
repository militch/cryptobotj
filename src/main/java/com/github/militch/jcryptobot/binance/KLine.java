package com.github.militch.jcryptobot.binance;

public class KLine {
    /**
     * 开盘时间
     */
    private Long openTime;
    /**
     * 收盘时间
     */
    private Long closeTime;
    /**
     * 开盘价
     */
    private String openPrice;
    /**
     * 收盘价
     */
    private String closePrice;
    /**
     * 最高价
     */
    private String highPrice;
    /**
     * 最低价
     */
    private String lowPrice;
    /**
     * 成交量
     */
    private String volume;
    /**
     * 成交额
     */
    private String quoteVolume;
    /**
     * 成交笔数
     */
    private Long count;
    /**
     *
     */
    private String bidPrice;
    private String bidQty;

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(String quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getBidQty() {
        return bidQty;
    }

    public void setBidQty(String bidQty) {
        this.bidQty = bidQty;
    }

    @Override
    public String toString() {
        return "KLine{" +
                "openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", openPrice='" + openPrice + '\'' +
                ", closePrice='" + closePrice + '\'' +
                ", highPrice='" + highPrice + '\'' +
                ", lowPrice='" + lowPrice + '\'' +
                ", volume='" + volume + '\'' +
                ", quoteVolume='" + quoteVolume + '\'' +
                ", count=" + count +
                ", bidPrice='" + bidPrice + '\'' +
                ", bidQty='" + bidQty + '\'' +
                '}';
    }
}
