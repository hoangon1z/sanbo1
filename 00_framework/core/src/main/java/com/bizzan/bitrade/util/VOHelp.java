package com.bizzan.bitrade.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wmf
 * @date 2019-6-4 14:59:48
 */
@Slf4j
public class VOHelp {

    @SuppressWarnings("unchecked")
    public static <D> List<D> listToVO(List r, Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Field> fields = getAllFields(clazz, new ArrayList<>());
        Class[] classes = new Class[fields.size()];
        for (int j = 0; j < fields.size(); j++) {
            classes[j] = fields.get(j).getType();
        }
        List<D> ds = new ArrayList<>();
        Constructor constructor = clazz.getConstructor(classes);
        for (Object obj : r) {
            Object[] arr = (Object[]) obj;
            D d = (D) constructor.newInstance(arr);
            ds.add(d);
        }
        return ds;
    }

    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}
