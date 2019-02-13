package com.wsq.orm.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 默认WsqDao实现
 *
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-18 10:28
 */
public class DefaultBaseDao<T> extends AbstractBaseDao {

    public <E> E queryByPrimary(Long primaryKey) throws SQLException {
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put(getPrimarykeyColumnName(), primaryKey);
        List<E> result = this.queryBySqlId(sqlConfigBean.getPrimaryKey(), parameters);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    public int updateBySqlId(String sqlId, Map parametersValues) throws SQLException {
        return executeUpdate(sqlId, parametersValues);
    }

    public int deleteBySqlId(String sqlId, Map parametersValues) throws SQLException {
        return executeUpdate(sqlId, parametersValues);
    }

    public <E> List<E> queryBySqlId(String sqlId, Map<String, Object> parameters) throws SQLException {
        return super.queryBySqlId0(sqlId, parameters);
    }

    public <E> List<E> queryBySqlId(String sqlId) throws SQLException {
        return queryBySqlId(sqlId,null);
    }

    @Override
    public void insert(String sqlId, Object parameter) throws Exception {
        List<Object> parameters = new ArrayList<>();
        parameters.add(parameter);
        super.insertBatch(sqlId, parameters, 0, 1);
    }

    @Override
    public void insertBatch(String sqlId, List parameters, int from, int to) throws Exception {
        super.insertBatch(sqlId, parameters, from, to);
    }
}
