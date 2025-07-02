package com.kuzetech.bigdata.json.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class FastJsonUtil {

    //Map转成实体对象
    public static Object mapToObject(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                String flag = (String) map.get(field.getName());
                if (flag != null) {
                    if (flag.equals("false") || flag.equals("true")) {
                        field.set(obj, Boolean.parseBoolean(flag));
                    } else {
                        field.set(obj, map.get(field.getName()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
