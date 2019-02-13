package com.wsq.orm.dao;

import com.wsq.orm.SqlInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *  WsqDao接口
 *
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-18 10:51
 */
public interface BaseDao {

    /**
     * 按照操作类型批量增加
     * @param sqlId  操作类型
     * @param parameters  批量操作对象集合
     * @throws Exception Exception
     */
    void insertBatch(String sqlId, List parameters) throws Exception;

    /**
     * 按照操作类型增加
     * @param sqlId  操作类型
     * @param parameter  操作对象
     * @throws Exception Exception
     */
    void insert(String sqlId, Object parameter) throws Exception;

    /**
     * 批量添加
     * @param sqlId  操作类型
     * @param parameters  批量操作对象集合
     * @param from  起始位置
     * @param to    终止位置（不包含）
     * @throws Exception Exception
     */
    void insertBatch(String sqlId, List parameters, int from, int to) throws Exception;

    /**
     * update insert delete 操作
     * @param sqlId 操作id
     * @param parametersValues  入参值
     * @return 影响数目
     * @throws SQLException Exception
     */
    int executeUpdate(String sqlId, Map parametersValues) throws SQLException;

    /**
     * 查询 sqlInfo
     * @param sqlInfo  sql信息
     * @param <E>      返回类型
     * @return         返回List
     * @throws SQLException e
     */
    <E> List<E> query(SqlInfo sqlInfo) throws SQLException;
}
