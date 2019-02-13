package com.wsq.orm.config.sql;

import com.wsq.orm.config.ConfigBean;
import com.wsq.orm.org.apache.ibatis.io.ResolverUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * sql配置类
 *
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-25 16:46
 */
@ConfigurationProperties(prefix = SqlResultTypeConfigBean.PREFIX)
public class SqlResultTypeConfigBean {

    static final String PREFIX = ConfigBean.PREFIX + ".result";

    private String basePackage = "";

    private Map<String,Class> type = new HashMap<>(256);

    private Map<String,String> className = new HashMap<>(256);

    {
        this.type.put("String",String.class);
        this.type.put("string",String.class);
        this.type.put("Integer",Integer.class);
        this.type.put("int",Integer.class);
        this.type.put("Double",Double.class);
        this.type.put("double",Double.class);
        this.type.put("Float",Float.class);
        this.type.put("float",Float.class);
        this.type.put("Long",Long.class);
        this.type.put("long",Long.class);
        this.type.put("Character",Character.class);
        this.type.put("char",Character.class);
        this.type.put("Short",Short.class);
        this.type.put("short",Short.class);
        this.type.put("Boolean",Boolean.class);
        this.type.put("boolean",Boolean.class);
        this.type.put("Byte",Byte.class);
        this.type.put("byte",Byte.class);
        this.type.put("Void",Void.class);
        this.type.put("Date",Date.class);
        this.type.put("Timestamp", Timestamp.class);
        this.type.put("Time", Time.class);
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        if (!StringUtils.isBlank(this.getBasePackage())) {
            ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
            resolverUtil.find(new ResolverUtil.IsA(Object.class), basePackage);
            Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
            for(Class<?> type : typeSet){
                // Ignore inner classes and interfaces (including package-info.java)
                // Skip also inner classes. See issue #6
                if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
                    this.type.put(type.getSimpleName(),type);
                }
            }
        }
        for (Map.Entry<String, Class> entry : type.entrySet()) {
            className.put(entry.getKey(), entry.getValue().getName());
        }
    }

    public String getBasePackage() {
        return basePackage;
    }

    public Map<String, Class> getType() {
        return type;
    }

    public void setType(Map<String, Class> type) {
        this.type = type;
    }

    public Map<String, String> getClassName() {
        return className;
    }

    public void setClassName(Map<String, String> className) {
        this.className = className;
    }
}
