package com.wsq.orm.config.sql;

import com.wsq.orm.config.ConfigBean;
import com.wsq.orm.resolver.AbstractSqlResolver;
import com.wsq.orm.resolver.DefaultSqlResolver;
import com.wsq.orm.utils.Assert;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * sql配置类
 *
 * @author wangshuangquan<wangshuangquan-a @ qq.com>
 * @date 2019-01-16 21:01
 */
@ConfigurationProperties(prefix = SqlConfigBean.PREFIX)
public class SqlConfigBean {

    static final String PREFIX = ConfigBean.PREFIX + ".sql";

    private boolean enabled = true;

    /**
     * tableName: 表名，唯一，多个以配置后一个为准；
     * sqlId: 操作id.例如：i-orderId
     * Map<tableName,Map<sqlId,sql>>
     */
    private Map<String, Map<String,String>> sqlMap = new HashMap<>();

    private Map<String,Class> resultType = new HashMap<>();

    private Map<String,String> tables = new HashMap<>();

    private String primaryKey = "primaryKey";

    private Class<? extends AbstractSqlResolver> resolver = DefaultSqlResolver.class;
    /**
     * 大小写敏感
     */
    private boolean caseSensitive = false;

    private int batchMaxSize = 200000;

    public Map<String, String> getSQLsByTable (String tableName) {
        Map<String, String> tableSQLs = sqlMap.get(tableName);
        Assert.notNull(tableSQLs,"you must write tableName sql in e.g application-wsq.yml -> wsq: sql: sqlMap: t_user");
        return tableSQLs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Map<String, String>> getSqlMap() {
        return sqlMap;
    }

    public void setSqlMap(Map<String, Map<String, String>> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public Map<String, Class> getResultType() {
        return resultType;
    }

    public void setResultType(Map<String, Class> resultType) {
        this.resultType = resultType;
    }

    public Map<String, String> getTables() {
        return tables;
    }

    public void setTables(Map<String, String> tables) {
        this.tables = tables;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Class<? extends AbstractSqlResolver> getResolver() {
        return resolver;
    }

    public void setResolver(Class<? extends AbstractSqlResolver> resolver) {
        this.resolver = resolver;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int getBatchMaxSize() {
        return batchMaxSize;
    }

    public void setBatchMaxSize(int batchMaxSize) {
        this.batchMaxSize = batchMaxSize;
    }
}
