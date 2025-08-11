/**
 * File: DatabasePool
 * Author: will.liu
 * Date: 2025/8/11 18:19
 * Description: mysql连接池管理
 */
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DatabasePool {
    private static final HikariDataSource dataSource;

    static {
        // 从配置文件加载参数
        Properties props = new Properties();
        try (InputStream input = DatabasePool.class.getClassLoader().getResourceAsStream("default.properties")) {
            if (input == null) throw new RuntimeException("配置文件未找到");
            props.load(input);

        } catch (Exception e) {
            throw new RuntimeException("配置加载失败", e);
        }

        // 初始化连接池
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("max.pool.size", "20")));

        // 可选高级配置
        config.addDataSourceProperty("cachePrepStmts",
                props.getProperty("cache.prep.stmts", "true"));
        config.addDataSourceProperty("prepStmtCacheSize",
                props.getProperty("prep.stmt.cache.size", "250"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit",
                props.getProperty("prep.stmt.cache.sql.limit", "2048"));

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}