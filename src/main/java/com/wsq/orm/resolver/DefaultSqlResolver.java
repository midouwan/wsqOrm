package com.wsq.orm.resolver;


import com.wsq.orm.dao.AbstractBaseDao;

import java.util.*;

import static com.wsq.orm.SqlConstant.*;


/**
 * 默认sql解析器 条件格式:#{}
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-20 15:53
 */
public class DefaultSqlResolver extends AbstractSqlResolver{

    private static final String REGX_TO_CONDITION_SQL = "#\\{[a-zA-Z_]*}";
    /**
     * 条件前缀
     */
    public static final String CONDITION_PREFIX = "#{";
    /**
     * 条件后缀
     */
    public static final String CONDITION_SUFFIX = "}";

    public DefaultSqlResolver(AbstractBaseDao wsqDao) {
        super(wsqDao);
    }

    @Override
    String getExecuteSql (String sql,List<String> columns) {
        String upperSql = sql.toUpperCase();
        if (upperSql.startsWith(SELECT)) {
            int fromIndex = upperSql.indexOf(FROM);
            if (fromIndex != -1) {
                String tmp = upperSql.substring(SELECT.length(), fromIndex).trim();
                if (ALL_COLUMNS.equals(tmp)) {
                    StringBuilder sb = new StringBuilder();
                    int count = 1;
                    for (String column : columns) {
                        sb.append(column);
                        if (count != columns.size()) {
                            sb.append(COMMA);
                        }
                        count ++;
                    }
                    sql = sql.replaceAll("\\*", sb.toString());
                }
            }

        }
        // sql条件标准化
        return sql.replaceAll(REGX_TO_CONDITION_SQL, QUESTION_MARK);
    }

    @Override
    List<String> getResultColumnNames(String sql) {
        List<String> resultColumnNames = null;
        String sqlUpper = sql.toUpperCase();
        if (sqlUpper.startsWith(SELECT)) {
            int from = sqlUpper.indexOf(FROM);
            String tmp = sql.substring(SELECT.length(), from).trim();
            if (ALL_COLUMNS.equals(tmp)) {
                resultColumnNames = getColumnNamesOfAll();
            } else {
                resultColumnNames = Arrays.asList(tmp.split(COMMA));
            }
        } else if (sqlUpper.startsWith(INSERT)) {
            int lIndex = sqlUpper.indexOf(LEFT_PARENTHESIS);
            int valuesIndex = sqlUpper.indexOf(VALUES);

            if (lIndex < valuesIndex) {
                int rIndex = sqlUpper.indexOf(RIGHT_PARENTHESIS);
                resultColumnNames = Arrays.asList(sql.substring(lIndex+1, rIndex).trim().split(COMMA));
            } else {
                resultColumnNames = getColumnNamesOfAll();
            }
        }
        return resultColumnNames;
    }

    @SuppressWarnings("unchecked")
    private List<String> getColumnNamesOfAll() {
        Map columnFileTable =  this.getWsqDao().columnFileTable;
        Iterator<String> keys = columnFileTable.keySet().iterator();
        List<String> columns = new ArrayList<>(columnFileTable.size());
        while (keys.hasNext()) {
            columns.add(keys.next());
        }
        return columns;
    }

    @Override
    List<String> getParameterNames(String sql) {
        List<String> parameters = new ArrayList<>();
        int fromIndex;
        int index = 0;
        while ((fromIndex = sql.indexOf(CONDITION_PREFIX,index)) != -1) {
            index = sql.indexOf(CONDITION_SUFFIX, fromIndex);
            if (index != -1) {
                parameters.add(sql.substring(fromIndex + 2, index).trim());
            }
        }
        return parameters;
    }
}
