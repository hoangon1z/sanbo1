package com.bizzan.bitrade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author admin
 * @date 2019-6-4 13:52:30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    // wmf 2017/8/7 基本对象的属性名
    String propName() default "";

    // wmf 2019/11/30 原生字段用于拼接sql
    String nativeName() default "";

    // wmf 2017/8/7 查询方式
    Type type() default Type.EQUAL;

    enum Type {
        // admin 2019/6/4 相等
        EQUAL
        // admin 2017/8/7 大于等于
        , GREATER_THAN
        // admin 2017/8/7 小于等于
        , LESS_THAN
        // admin 2017/8/7 中模糊查询
        , INNER_LIKE
        // admin 2017/8/7 左模糊查询
        , LEFT_LIKE
        // admin 2017/8/7 右模糊查询
        , RIGHT_LIKE
        // admin 2017/8/7 小于
        , LESS_THAN_NQ
        // admin 2017/8/7 大于
        , GREATER_THAN_NQ
        // admin 2019/6/4 包含
        , IN
        // wmf 2020/1/2 忽略
        , IGNORE
    }

}

