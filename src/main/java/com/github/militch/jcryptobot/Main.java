package com.github.militch.jcryptobot;

import com.github.militch.jcryptobot.binance.BinanceClient;
import com.github.militch.jcryptobot.config.Config;
import com.github.militch.jcryptobot.jsonrpc.JsonRPCServer;
import com.github.militch.jcryptobot.mapper.MarketKLineMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String DEFAULT_APP_CONF = "application.properties";
    private static void serverRun(Config conf) throws IOException {
        String host = conf.getString("server.host", "127.0.0.1");
        int port = conf.getInt("server.port", 8091);
        JsonRPCServer server = new JsonRPCServer();
        server.registerMethod("echo");
        server.run(host, port);
        server.close();
    }

    public static Config loadConfig(String file) throws IOException {
        return Config.load(file);
    }
    private static DataSource getDataSource(Config appConfig){
        String host = appConfig.getString("datasource.host", "127.0.0.1");
        int port = appConfig.getInt("datasource.port", 3306);
        String databaseName = appConfig.getString("datasource.database");
        String jdbcUrl = appConfig.getString("datasource.url");
        if (jdbcUrl == null) {
            jdbcUrl = String.format("jdbc:mysql://%s:%d/%s",host, port, databaseName);
        }
        String driverClassName = appConfig.getString("datasource.driver_class_name",
                "com.mysql.cj.jdbc.Driver");
        String username = appConfig.getString("datasource.username");
        String password = appConfig.getString("datasource.password");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName(driverClassName);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }
    public static SqlSessionFactory createSqlSessionFactory(Config config){
        DataSource ds = getDataSource(config);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment env = new Environment("production", transactionFactory, ds);
        Configuration configuration = new Configuration(env);
        configuration.addMapper(MarketKLineMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }
    public static void main(String[] args) throws IOException {
        Config config = loadConfig(DEFAULT_APP_CONF);
        String apiKey = config.getString("binance.api_key");
        String secretKey = config.getString("binance.secret_key");
        SqlSessionFactory sf = createSqlSessionFactory(config);
        BinanceClient binanceClient = new BinanceClient(apiKey, secretKey);
        TradeTrackingTask tradeTrackingTask = new TradeTrackingTask(binanceClient, sf);
        Timer t = new Timer();
        t.schedule(tradeTrackingTask, 0, 1000);
        //t.schedule(tradeTrackingTask, 0);
        serverRun(config);
    }
}
