package com.wsq.orm.resolver;

import com.wsq.orm.SqlInfo;
import com.wsq.orm.dao.AbstractBaseDao;

import java.util.List;

/**
 * 抽象sql解析器
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-22 16:30
 */
public abstract class AbstractSqlResolver {

    private static final int SQL_SPLIT_SIZE = 2;
    private static final String SQL_SPLIT = ":";
    private AbstractBaseDao wsqDao;

    AbstractSqlResolver(AbstractBaseDao wsqDao) {
        this.wsqDao = wsqDao;
    }

    /**
     * 获取sql的条件列表
     * @param sql sql
     * @return 条件列表
     */
    abstract List<String> getParameterNames(String sql);

    /**
     * 获取可执行sql
     * @param sql Sql
     * @param columns 查询列
     * @return 执行sql
     */
    abstract String getExecuteSql(String sql,List<String> columns);

    /**
     * 获取sql查询列
     * @param sql  Sql
     * @return  sql查询列
     */
    abstract List<String> getResultColumnNames(String sql);

    public final SqlInfo getSqlInfo(String sqlId, String sql) {
        List<String> resultColumnNames = getResultColumnNames(sql);
        String[] split = sql.split(SQL_SPLIT);
        Class clazz = null;
        if (split.length == SQL_SPLIT_SIZE) {
            sql = split[0].trim();
            clazz = this.wsqDao.sqlResultTypeConfigBean.getType().get(split[1].trim());
        } else if (split.length > SQL_SPLIT_SIZE) {
            throw new RuntimeException("The sqlId:" + sqlId +  "`sql : exception");
        }
        return new SqlInfo(sqlId, resultColumnNames, getParameterNames(sql),getExecuteSql(sql,resultColumnNames),clazz);
    }

    AbstractBaseDao getWsqDao() {
        return wsqDao;
    }
}
