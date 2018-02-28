package org.leave.framework;

import org.leave.framework.helper.BeanHelper;
import org.leave.framework.helper.ClassHelper;
import org.leave.framework.helper.ControllerHelper;
import org.leave.framework.helper.IocHelper;
import org.leave.framework.util.ClassUtil;

public final class HelperLoader {
    public static void init(){
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList){
            //TODO
            ClassUtil.loadClass(cls.getName(), false);
        }
    }
}
