package com.wsq.orm;


import com.wsq.orm.utils.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射工具类
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-19 18:16
 */
public class ReflectUtil {

    /**
     * 获取泛型类
     * @param clazz 当前类
     * @return 泛型列表
     */
    public static Type[] getTClass(Class clazz) {
        Type superclass = clazz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            Type[] genericTypes = ((ParameterizedType) superclass).getActualTypeArguments();
            Assert.notNull(genericTypes,"Please " + clazz.getName() + " extends com.wsq.award.importer.orm.dao.AbstractBaseDao");
            return genericTypes;
        }
        return null;
    }

    /**
     * 字符串首字母大写
     * @param source 原始字符串
     * @return 首字母转换为大写的字符串
     */
    public static String firstCharToUpperCase(String source) {
        char firstChar = source.charAt(0);
        if (Character.isLetter(firstChar)) {
            return Character.toUpperCase(firstChar) + source.substring(1);
        }
        throw new IllegalArgumentException("字符串:"+source+",首字符不是字母");
    }

    /**
     * 驼峰转换大写字母变_小写（orderId-->order_id）
     * @param field 实体字段
     * @return  驼峰转换大写字母变_小写后的字符串
     */
    public static String columnName2LowerCase(Field field) {
        StringBuilder sb = new StringBuilder();
        char[] chars = field.getName().toCharArray();
        for (char aChar : chars) {
            // 驼峰转换大写字母变_小写
            if (Character.isUpperCase(aChar)) {
                sb.append("_");
                sb.append(Character.toLowerCase(aChar));
                continue;
            }
            sb.append(aChar);
        }
        return sb.toString();
    }
}
