package com.github.militch.jcryptobot.mapper;

import com.github.militch.jcryptobot.model.MarketKLine;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDateTime;

public interface MarketKLineMapper {

    @InsertProvider(type = MarketKLineSqlProvider.class, method = "insert")
    int insert(MarketKLine kLine);

    @Select("SELECT * FROM market_kline WHERE id=#{id}")
    @Results(id = "marketKLineResult", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "exchange", column = "exchange"),
            @Result(property = "symbol", column = "symbol"),
            @Result(property = "decimals", column = "decimals"),
            @Result(property = "openTime", column = "open_time"),
            @Result(property = "closeTime", column = "close_time"),
            @Result(property = "openPrice", column = "open_price"),
            @Result(property = "closePrice", column = "close_price"),
            @Result(property = "highPrice", column = "high_price"),
            @Result(property = "lowPrice", column = "low_price"),
            @Result(property = "volume", column = "volume"),
            @Result(property = "quoteVolume", column = "quote_volume"),
            @Result(property = "count", column = "t_count"),
            @Result(property = "createTime", column = "create_time"),
    })
    MarketKLine selectOneById(@Param("id") Integer id);

    @Select("SELECT count(1) FROM market_kline WHERE `open_time` = #{time}")
    int countByOpenTime(@Param("time") LocalDateTime time);

    class MarketKLineSqlProvider implements ProviderMethodResolver {
        public static String insert(MarketKLine kLine) {
            return new SQL(){{
                INSERT_INTO("market_kline");
                VALUES("exchange", "#{exchange}");
                VALUES("symbol", "#{symbol}");
                VALUES("decimals", "#{decimals}");
                VALUES("open_time", "#{openTime}");
                VALUES("close_time", "#{closeTime}");
                VALUES("open_price", "#{openPrice}");
                VALUES("close_price", "#{closePrice}");
                VALUES("high_price", "#{highPrice}");
                VALUES("low_price", "#{lowPrice}");
                VALUES("volume", "#{volume}");
                VALUES("t_count", "#{count}");
                VALUES("quote_volume", "#{quoteVolume}");
                VALUES("create_time", "#{createTime}");
            }}.toString();
        }
    }
}
