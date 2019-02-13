# wsqORM核心功能

## *完美结合springboot，原来sql复杂的xml配置文件由yml代替,从繁琐的sql配置中解放<br />
## *待续。。。<br />

使用前最好使用最新版,mvn仓库没有可以下载源码打包

## 1.引入jar
```
<dependency>
	<groupId>com.wsq</groupId>
	<artifactId>orm</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>

```

## 2.java数据映射实体模型
```
@Setter
@Getter
@Table(name = "T_TEMPLATE")
public class Template {

    @Column(jdbcType = JDBCType.INTEGER,primaryKey = true)
    private Integer no;

    @Column
    private String name;
}
```
##### @Column注解标识实体列信息，jdbcType为数据库JDBCType,默认为varChar,primaryKey主键标识默认为false

## 3.dao继承defaultBaseDao,泛型为当前实体
```
@Repository
public class TemplateDao extends DefaultBaseDao<Template> {
}
```
## 4.配置数据源，与springboot一致，不做介绍

## 5.在yml文件配置sql，可以单独配置一个文件如application-wsq.yml，导入使用include
```
wsq:
  result:
    base-package: "com.wsq.XX.XX.pojo" #扫描实体根包
  sql:
    resolver: com.wsq.orm.resolver.DefaultSqlResolver #sql解析器，默认有可不配置，默认解析器使用#{}表示参数，可自定义解析器
    batch-max-size: 1000000 #批量操作最大数，默认20万
    tables:  #sql表名，方便sql统一管理,可不写
      FAJIANG: fajiang
      T_TEMPLATE: T_TEMPLATE
      T_TEMPLATE_INFO: T_TEMPLATE_INFO
    sql-map:    #sql 组成 sqlId（同一表下唯一，多个后者覆盖前者）："sql主体:返回值类型（实体类simpleName）"
      fajiang:  #表名
        q_byOrderIdAndPhoneNumber: "select * from ${wsq.sql.tables.FAJIANG} where order_id = #{order_id}"
        q_all: "select * from ${wsq.sql.tables.FAJIANG} where rownum < 100 : Fajiang"
        q_ByPhoneNumber: "select * from ${wsq.sql.tables.FAJIANG} where phone_number = #{phone_number}"
        i_batch: "insert into ${wsq.sql.tables.FAJIANG} (order_id,phone_number,info_num) values (#{order_id},#{phone_number},#{info_num})"
        q_byInfoNumAndStatus: "select * from ${wsq.sql.tables.FAJIANG} where status = 1 and info_num = #{info_num}"
        u_orderIdByPhoneNumber: "update ${wsq.sql.tables.FAJIANG} set order_id = #{order_id} where phone_number = #{phone_number}"
        d_ByOrderId: "delete from fajiang where order_id = #{order_id}"
      T_TEMPLATE:
        q_all: "select * from ${wsq.sql.tables.T_TEMPLATE}"
      T_TEMPLATE_INFO:
        q_byNo: "select * from ${wsq.sql.tables.T_TEMPLATE_INFO} where no = #{no} order by sort asc:TemplateInfo"
```
## 6.事务支持
```
入口类配置
@EnableTransactionManagement支持spring注解式事务
```
## 7.支持CRUD
```
//插入

//批量插入
awardDao.insertBatch("i_batch",parameters);

//主键查询
awardDao.queryByPrimary(id);
//有参查询
awardDao.queryBySqlId(sqlId,parameters);
//无参查询
awardDao.queryBySqlId("q_all");

//更新
awardDao.updateBySqlId("sqlId",parametersMap);

//删除
awardDao.deleteBySqlId("sqlId",parameters);
```
