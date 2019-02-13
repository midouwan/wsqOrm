package com.wsq.orm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * sql信息 e.g. q_ByOrderId: Select * from orderId=#{orderId}:object
 * @author wangshuangquan<wangshuangquan-a @ qq.com>
 * @date 2019-01-20 15:46
 */

@Setter
@Getter
@ToString
public class SqlInfo {


    private AtomicBoolean finished = new AtomicBoolean(false);
    /**
     * sql操作类型，同一实体不可重复，后者覆盖前者
     */
    private String sqlId;
    /**
     * 标准sql
     */
    private String sql;
    /**
     * 返回值列表名称，e.g. *
     */
    private List<String> resultColumnNames;
    /**
     * 入参名称
     */
    private List<String> parameterNames;
    /**
     * 返回值类型
     */
    private Class resultType;
    /**
     * column,value
     */
    @Setter(value = AccessLevel.PRIVATE)
    private ThreadLocal<Map<String,Object>> valueMap = new ThreadLocal<>();

    @Setter(value = AccessLevel.PRIVATE)
    private ThreadLocal<List<Object>> valueList = new ThreadLocal<>();

    public SqlInfo(String sqlId, List<String> resultColumnNames, List<String> parameterNames, String executeSql, Class resultType) {
        this.sqlId = sqlId;
        this.resultColumnNames = resultColumnNames;
        this.parameterNames = parameterNames;
        this.sql = executeSql;
        this.resultType = resultType;
    }

    @SuppressWarnings("unchecked")
    public void setValues(Object object) {
        if (object instanceof Map) {
            valueMap.set((Map<String, Object>) object);
        } else if (object instanceof List){
            valueList.set((List<Object>) object);
        }
    }

    public void valueClear() {
        valueMap.remove();
        valueList.remove();
    }
}
