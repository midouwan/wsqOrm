package com.wsq.orm.dao;

import com.wsq.orm.config.sql.SqlConfigBean;
import com.wsq.orm.resolver.AbstractSqlResolver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

/**
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-02-01 19:09
 */
class InitDao implements InitializingBean {

    @Autowired
    private DataSource dataSource;

    @Autowired
    protected SqlConfigBean sqlConfigBean;

    AbstractSqlResolver sqlResolver;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 初始化hikariCp连接池,避免初次加载耗时
        if ("com.zaxxer.hikari.HikariDataSource".equals(dataSource.getClass().getName())) {
            initHikariCp();
        }
        initResolver();
        // 初始化@Table,@Column注解信息
        initTableInfo();
        // 根据表名获取当前对象对应sql
        initTableSQL();
    }

    protected void initHikariCp() {

    }

    protected void initResolver() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

    }

    /**
     * 根据表名初始化sql
     */
    protected void initTableSQL() {

    }

    protected void initTableInfo() {

    }

    final Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    final void closeConnection(Connection connection) {
        DataSourceUtils.releaseConnection(connection,dataSource);
    }
}
