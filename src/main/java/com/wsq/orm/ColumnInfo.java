package com.wsq.orm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.JDBCType;

/**
 * 列信息
 * @author wangshuangquan<wangshuangquan-a @ qq.com>
 * @date 2019-01-16 15:53
 */
@Setter
@Getter
@ToString
public class ColumnInfo {

    private String fieldName;
    private String columnName;
    private JDBCType type;

    public ColumnInfo(String fieldName, String columnName, JDBCType type) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.type = type;
    }
}
