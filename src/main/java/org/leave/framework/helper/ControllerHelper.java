package org.leave.framework.helper;

import org.leave.framework.annotation.Action;
import org.leave.framework.bean.Handler;
import org.leave.framework.bean.Request;
import org.leave.framework.util.ArrayUtil;
import org.leave.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 */
public final class ControllerHelper {
    /**
     * 用于存放请求与处理器映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有的controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpay(controllerClassSet)){
            for (Class<?> controllerClass : controllerClassSet){
                //获取controller类中定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if(ArrayUtil.isNotEmpty(methods)){
                    for (Method method : methods){
                        //判断当前方法是否带有action注解
                        if (method.isAnnotationPresent(Action.class)){
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            //验证url映射规则
                            if (mapping.matches("\\w+:/\\w")){
                                String[] array = mapping.split(":");
                                if (ArrayUtil.isNotEmpty(array) && array.length == 2){
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod, requestPath);
                                    Handler handle = new Handler(controllerClass, method);
                                    //初始化action map
                                    ACTION_MAP.put(request, handle);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取handle
     */
    public static Handler getHandle(String requestMethod, String requestPath){
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
