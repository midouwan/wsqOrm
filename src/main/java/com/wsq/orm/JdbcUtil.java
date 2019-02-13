package com.wsq.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wangshuangquan<wangshuangquan-a @ qq.com>
 * @date 2019-01-16 10:56
 */
public class JdbcUtil {

    public static void close(Connection connection, Statement stat) {
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeConnection(Connection connection) {
        close(connection,null);
    }

    public static void closeStatement(Statement stat) {
        close(null,stat);
    }
}
