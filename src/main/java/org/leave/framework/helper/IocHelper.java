package org.leave.framework.helper;

import org.leave.framework.annotation.Inject;
import org.leave.framework.util.ArrayUtil;
import org.leave.framework.util.CollectionUtil;
import org.leave.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

public final class IocHelper {
    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpay(beanMap)){
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()){
                //从beanMap中获取bean类和bean实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                //获取bean类兴义的所有成员变量
                Field[] fields  = beanClass.getDeclaredFields();
                if(ArrayUtil.isNotEmpty(fields)){
                    for(Field beanField : fields){
                        //判断bean field是否带有inject注解
                        if(beanField.isAnnotationPresent(Inject.class)){
                            //bean map中获取 bean field 的实例
                            Class<?> beanFieldCLass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldCLass);
                            if(beanFieldInstance != null){
                                //通过反射初始化beanField的值
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
