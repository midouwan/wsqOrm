package com.wsq.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;

/**
 * 列注解
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-18 11:16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default "";

    JDBCType jdbcType() default JDBCType.VARCHAR;

    boolean primaryKey() default false;
}
