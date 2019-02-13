package com.wsq.orm.dao;

import com.wsq.orm.Column;
import com.wsq.orm.ColumnInfo;
import com.wsq.orm.SqlInfo;
import com.wsq.orm.config.sql.SqlResultTypeConfigBean;
import com.wsq.orm.resolver.AbstractSqlResolver;
import com.wsq.orm.resolver.DefaultSqlResolver;
import com.wsq.orm.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Table;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wsq.orm.ReflectUtil.*;
import static com.wsq.orm.resolver.DefaultSqlResolver.CONDITION_PREFIX;
import static com.wsq.orm.resolver.DefaultSqlResolver.CONDITION_SUFFIX;
import static java.sql.Types.NULL;

/**
 * WsqDao抽象类
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-17 14:09
 */
@Slf4j
public abstract class AbstractBaseDao<T> extends InitDao implements BaseDao {


    private Map<String, SqlInfo> sqlIdSqlInfoTable = new HashMap<>(16);

    /**
     * <字段,列信息>
     */
    private Map<String, ColumnInfo> columnTable = new HashMap<>(16);

    /**
     * 格式：列名,字段名
     */
    public Map<String,String> columnFileTable = new HashMap<>(16);

    public SqlResultTypeConfigBean sqlResultTypeConfigBean;

    private Class<T> tableClass;

    private static AtomicBoolean hikariCpInit = new AtomicBoolean(false);

    private String tableName;

    @Autowired
    private void setSqlConfigBean(SqlResultTypeConfigBean sqlResultTypeConfigBean){
        this.sqlResultTypeConfigBean = sqlResultTypeConfigBean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initResolver() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends AbstractSqlResolver> resolver = sqlConfigBean.getResolver();
        Class<? extends AbstractBaseDao> aClass = this.getClass();
        while (!Modifier.isAbstract(aClass.getModifiers())) {
            aClass = (Class<? extends AbstractBaseDao>) aClass.getSuperclass();
        }
        Constructor<? extends AbstractSqlResolver> constructor = resolver.getConstructor(aClass);
        this.sqlResolver = constructor.newInstance(this);
    }

    /**
     * 初始化hikariCp连接池,避免初次加载耗时
     */
    @Override
    public void initHikariCp() {
        if (!hikariCpInit.getAndSet(true)) {
            long startTime = System.currentTimeMillis();
            getConnection();
            System.out.println("HikariCp pool init time :" + (System.currentTimeMillis() - startTime)+"ms");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void initTableInfo() {
        // 获取泛型类
        Type[] tClasses = getTClass(this.getClass());
        Assert.notNull(tClasses,"Please " + this.getClass().getName() + " extends com.wsq.award.importer.orm.dao.AbstractBaseDao<T> T not null");
        // 此框架只有一个泛型类型
        this.tableClass = (Class) tClasses[0];
        Assert.notNull(tableClass,"Please " + this.getClass().getName() + " extends com.wsq.award.importer.orm.dao.AbstractBaseDao");

        // 获取泛型类的table注解，获得表名
        Table tableAnnotation = this.tableClass.getAnnotation(Table.class);
        Assert.notNull(tableAnnotation,"please set @javax.persistence.Table on " + this.tableClass.getName());
        this.tableName = tableAnnotation.name();
        Assert.notNull(tableName,"please set @javax.persistence.Table(name=tableName) on " + this.tableClass.getName() + ", tableName notNull!");
        addFields(this.tableClass,false);
    }

    /**
     * 根据表名初始化sql
     */
    @Override
    public final void initTableSQL() {
        Assert.notNull(sqlConfigBean,"please add SqlConfigBean to IOC that use @bean SqlConfigBean or other way ");
        Assert.notNull(tableClass,"Please " + this.getClass().getName() + " extends com.wsq.award.importer.orm.dao.AbstractBaseDao");
        Assert.notNull(tableName,"please set @javax.persistence.Table(name=tableName) on " + this.tableClass.getName() + ", tableName notNull!");
        Map<String, String> sqlTable = this.sqlConfigBean.getSQLsByTable(this.tableName);
        ColumnInfo columnInfo = this.columnTable.get(sqlConfigBean.getPrimaryKey());
        if (columnInfo != null && this.sqlResolver instanceof DefaultSqlResolver) {
            String primaryKeyColumn = columnInfo.getColumnName();
            sqlTable.put(sqlConfigBean.getPrimaryKey(),"select * from " + tableName + " where " + primaryKeyColumn +" =" + CONDITION_PREFIX +  primaryKeyColumn + CONDITION_SUFFIX);
        }
        for (Map.Entry<String, String> entry : sqlTable.entrySet()) {
            String sqlId = entry.getKey();
            SqlInfo sqlInfo = sqlResolver.getSqlInfo(sqlId, entry.getValue());
            this.sqlIdSqlInfoTable.put(sqlId, sqlInfo);
            // 放入不判断是否存在，存在则覆盖
            //this.sqlResolver.setResultTypeClass(sqlInfo.getResultType().getName(),sqlInfo.getResultType());
        }
    }

    /**
     * 将file添加到对应表中
     * @param tClass 类class
     */
    private void addFields(Class<?> tClass,boolean isParent) {
        Class<?> superclass = tClass.getSuperclass();
        if (superclass != null) {
            addFields(superclass,true);
        }
        Field[] fields = tClass.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (isParent && Modifier.isPrivate(field.getModifiers())) {
                    continue;
                }

                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String columnName = column.name();
                    if(StringUtils.isBlank(columnName)) {
                        columnName = columnName2LowerCase(field);
                    }
                    if (this.columnTable.containsKey(columnName)) {
                        log.warn("columnName had! will over");
                    }
                    ColumnInfo value = new ColumnInfo(field.getName(), columnName, column.jdbcType());
                    this.columnTable.put(columnName, value);
                    columnFileTable.put(columnName,field.getName());
                    if (column.primaryKey()) {
                        this.columnTable.put(sqlConfigBean.getPrimaryKey(), value);
                    }
                }
            }
        }
    }

    @Override
    public final void insertBatch(String sqlId, List parameters) throws Exception {
        insertBatch(sqlId, parameters,0,parameters.size());
    }
    private List<Object> resultSet2Result(SqlInfo sqlInfo, ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = sqlInfo.getResultType();
        if (clazz == null) {
            clazz = tableClass;
        }
        List<Object> result = new ArrayList<>();
        while (resultSet.next()) {
            Object obj;
            switch (clazz.getSimpleName()) {
                case "String":
                    obj = resultSet.getString(1);
                    break;
                case "Integer":
                    obj = resultSet.getInt(1);
                    break;
                case "Long":
                    obj = resultSet.getLong(1);
                    break;
                case "Short":
                    obj = resultSet.getShort(1);
                    break;
                case "Byte":
                    obj = resultSet.getByte(1);
                    break;
                case "Double":
                    obj = resultSet.getDouble(1);
                    break;
                case "Boolean":
                    obj = resultSet.getBoolean(1);
                    break;
                case "Float":
                    obj = resultSet.getFloat(1);
                    break;
                case "Time" :
                    obj = resultSet.getTime(1);
                    break;
                case "Timestamp":
                    obj = resultSet.getTimestamp(1);
                    break;
                case "Date":
                    obj = resultSet.getDate(1);
                    break;
                default:
                    obj = clazz.newInstance();
                    objectSetField(sqlInfo.getResultColumnNames(), resultSet, clazz, obj);
                    break;
            }
            result.add(obj);
        }
        return result;
    }

    private void setStat(PreparedStatement stat, List parameters, Map parameterValues) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            int parameterIndex = i + 1;
            Object value = parameterValues.get(parameters.get(i));

            log.info("\nparameter[" + parameterIndex + "]: " + parameters.get(i) + " :   " + value);
            if (value instanceof Long) {
                stat.setLong(parameterIndex, (Long) value);
            } else if (value instanceof String) {
                stat.setString(parameterIndex, (String) value);
            } else if (value instanceof Timestamp) {
                stat.setTimestamp(parameterIndex, (Timestamp) value);
            }
        }
    }

    private void objectSetField(List columns, ResultSet resultSet, Class clazz, Object instance) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Assert.notNull(clazz,"");
        for (int i = 1; i <= columns.size(); i++) {
            String option = (String) columns.get(i - 1);
            ColumnInfo columnInfo = this.columnTable.get(option);
            Object paramter = null;
            Class<?> parameterClass = null;
            switch (columnInfo.getType()) {
                case CHAR:
                case LONGVARCHAR:
                case NCHAR:
                case VARCHAR:
                case NVARCHAR:
                case LONGNVARCHAR:
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY:
                    parameterClass = String.class;
                    paramter = resultSet.getString(i);
                    break;
                case TIMESTAMP:
                    parameterClass = Timestamp.class;
                    paramter = resultSet.getTimestamp(i);
                    break;
                case BIT:
                case BOOLEAN:
                    parameterClass = Boolean.class;
                    paramter = resultSet.getBoolean(i);
                    break;
                case SMALLINT:
                    parameterClass = Short.class;
                    paramter = resultSet.getShort(i);
                    break;
                case TINYINT:
                case INTEGER:
                    parameterClass = Integer.class;
                    paramter = resultSet.getInt(i);
                    break;
                case BIGINT:
                    parameterClass = Long.class;
                    paramter = resultSet.getLong(i);
                    break;
                case REAL:
                    parameterClass = Float.class;
                    paramter = resultSet.getFloat(i);
                    break;
                case FLOAT:
                case DOUBLE:
                    parameterClass = Double.class;
                    paramter = resultSet.getDouble(i);
                    break;
                case NUMERIC:
                case DECIMAL:
                    parameterClass = BigDecimal.class;
                    paramter = resultSet.getBigDecimal(i);
                    break;
                case DATE:
                    parameterClass = Date.class;
                    paramter = resultSet.getDate(i);
                    break;
                case TIME:
                    parameterClass = Time.class;
                    paramter = resultSet.getTime(i);
                    break;
                case TIME_WITH_TIMEZONE:
                    parameterClass = Time.class;
                    paramter = resultSet.getTime(i,Calendar.getInstance(TimeZone.getDefault()));
                    break;
                case TIMESTAMP_WITH_TIMEZONE:
                    parameterClass = Timestamp.class;
                    paramter = resultSet.getTimestamp(i,Calendar.getInstance(TimeZone.getDefault()));
                    break;
                case JAVA_OBJECT:
                    parameterClass = Object.class;
                    paramter = resultSet.getObject(i);
                    break;
                case ARRAY:
                    parameterClass = Array.class;
                    paramter = resultSet.getArray(i);
                    break;
                case BLOB:
                    parameterClass = Blob.class;
                    paramter = resultSet.getBlob(i);
                    break;
                case CLOB:
                    parameterClass = Clob.class;
                    paramter = resultSet.getClob(i);
                    break;
                case REF:
                    parameterClass = Ref.class;
                    paramter = resultSet.getRef(i);
                    break;
                case ROWID:
                    parameterClass = RowId.class;
                    paramter = resultSet.getRowId(i);
                    break;

                case NCLOB:
                    parameterClass = NClob.class;
                    paramter = resultSet.getNClob(i);
                    break;
                case SQLXML:
                    parameterClass = SQLXML.class;
                    paramter = resultSet.getSQLXML(i);
                    break;
                default:
            }
            @SuppressWarnings("unchecked")
            Method method = clazz.getMethod("set" + firstCharToUpperCase(columnFileTable.get(option)), parameterClass);
            method.invoke(instance, paramter);
        }
    }

    private void setStat(PreparedStatement stat, int index, JDBCType jdbcType, Object filedValue) throws SQLException {
        switch (jdbcType) {
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
            case NCHAR:
            case NVARCHAR:
            case LONGNVARCHAR:
                stat.setString(index, (String) filedValue);
                break;
            case DATE:
                stat.setDate(index,(Date) filedValue);
                break;
            case TIME:
                stat.setTime(index,(Time) filedValue);
                break;
            case TIME_WITH_TIMEZONE:
                stat.setTime(index,(Time) filedValue, Calendar.getInstance(TimeZone.getDefault()));
                break;
            case TIMESTAMP:
                stat.setTimestamp(index, (Timestamp) filedValue);
                break;
            case TIMESTAMP_WITH_TIMEZONE:
                stat.setTimestamp(index,(Timestamp) filedValue, Calendar.getInstance(TimeZone.getDefault()));
                break;
            case BIT:
            case BOOLEAN:
                stat.setBoolean(index,(Boolean) filedValue);
                break;

            case TINYINT:
            case INTEGER:
                stat.setInt(index,(Integer) filedValue);
                break;

            case SMALLINT:
                stat.setShort(index,(Short) filedValue);
                break;
            case BIGINT:
                stat.setLong(index,(Long) filedValue);
                break;

            case FLOAT:
            case DOUBLE:
                stat.setDouble(index, (Double) filedValue);
                break;
            case REAL:
                stat.setFloat(index,(Float) filedValue);
                break;

            case NUMERIC:
            case DECIMAL:
                stat.setBigDecimal(index, (BigDecimal) filedValue);
                break;

            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
                stat.setString(index, (String) filedValue);
                break;
            case NULL:
                stat.setNull(index,NULL);
                break;
            case ARRAY:
                stat.setArray(index,(Array) filedValue);
                break;
            case BLOB:
                stat.setBlob(index,(Blob) filedValue);
                break;
            case CLOB:
                stat.setClob(index,(Clob) filedValue);
                break;
            case REF:
                stat.setRef(index,(Ref) filedValue);
                break;
            case ROWID:
                break;
            case NCLOB:
                stat.setNClob(index,(NClob) filedValue);
                break;
            case SQLXML:
                stat.setSQLXML(index,(SQLXML) filedValue);
                break;
            case REF_CURSOR:
                break;
            default:
                stat.setObject(index, filedValue);
                break;
        }
    }

    @Override
    public int executeUpdate(String sqlId, Map parametersValues) throws SQLException {
        SqlInfo sqlInfo = this.sqlIdSqlInfoTable.get(sqlId);
        List<String> parameterNames = sqlInfo.getParameterNames();
        sqlInfo.setValues(parametersValues);
        Connection connection = getConnection();
        try (PreparedStatement stat = connection.prepareStatement(sqlInfo.getSql())) {
            for (int index = 1; index <= parameterNames.size(); index++) {
                ColumnInfo columnInfo = columnTable.get(parameterNames.get(index - 1));
                setStat(stat, index, columnInfo.getType(), parametersValues.get(parameterNames.get(index - 1)));
            }
            return stat.executeUpdate();
        } finally {
            sqlInfo.valueClear();
            closeConnection(connection);
        }
    }

    final <E> List<E> queryBySqlId0(String sqlId, Map<String, Object> parameters) throws SQLException {
        SqlInfo sqlInfo = this.sqlIdSqlInfoTable.get(sqlId);
        sqlInfo.setValues(parameters);
        return query(sqlInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E> List<E> query(SqlInfo sqlInfo) throws SQLException {
        List<Object> query = this.query0(sqlInfo);
        sqlInfo.valueClear();
        return (List<E>) query;
    }

    private List<Object> query0(SqlInfo sqlInfo) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stat = connection.prepareStatement(sqlInfo.getSql())) {
            Map<String, Object> parameterValues = sqlInfo.getValueMap().get();
            if (parameterValues != null) {
                setStat(stat, sqlInfo.getParameterNames(), parameterValues);
            }
            try (ResultSet resultSet = stat.executeQuery()) {
                return resultSet2Result(sqlInfo,resultSet);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    final String getPrimarykeyColumnName() {
        ColumnInfo columnInfo = this.columnTable.get(sqlConfigBean.getPrimaryKey());
        Assert.notNull(columnInfo,"This Entity no have primaryKey,Please set @Column(primaryKey = true)");
        return columnInfo.getColumnName();
    }


    @Override
    public void insertBatch(String sqlId, List parameters, int from, int to) throws Exception {
        SqlInfo sqlInfo = this.sqlIdSqlInfoTable.get(sqlId);

        log.info("\nsql-->sqlId: " + sqlId + " : " + sqlInfo.getSql());
        List<String> resultColumnNames = sqlInfo.getResultColumnNames();
        sqlInfo.setValues(parameters);
        Connection connection = getConnection();
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        // PreparedStatement 线程不安全
        try (PreparedStatement stat = connection.prepareStatement(sqlInfo.getSql())) {
            // 数据库驱动是否支持批量操作
            boolean supportsBatchUpdates = connection.getMetaData().supportsBatchUpdates();
            if (supportsBatchUpdates && (to - from > sqlConfigBean.getBatchMaxSize())) {
                throw new RuntimeException("批量处理大小超额");
            }
            for (int i = from; i < to; i++) {
                Object t = parameters.get(i);
                for (int index = 1; index <= resultColumnNames.size(); index++) {
                    ColumnInfo columnInfo = columnTable.get(resultColumnNames.get(index - 1));
                    Object filedValue = t;
                    // TODO
                    if (!t.getClass().isPrimitive()) {
                        filedValue = t.getClass().getMethod("get" + firstCharToUpperCase(columnInfo.getFieldName())).invoke(t);
                    }
                    setStat(stat,index, columnInfo.getType(), filedValue);
                }
                if (supportsBatchUpdates) {
                    stat.addBatch();
                } else {
                    stat.executeUpdate();
                }
            }
            if (supportsBatchUpdates) {
                stat.executeBatch();
            }
        }finally {
            connection.setAutoCommit(autoCommit);
            sqlInfo.valueClear();
            closeConnection(connection);
        }
    }
}
