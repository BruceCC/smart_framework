package org.leave.framework;

import org.leave.framework.bean.Data;
import org.leave.framework.bean.Handler;
import org.leave.framework.bean.Param;
import org.leave.framework.bean.View;
import org.leave.framework.helper.BeanHelper;
import org.leave.framework.helper.ConfigHelper;
import org.leave.framework.helper.ControllerHelper;
import org.leave.framework.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
       //初始化helper类
        HelperLoader.init();
        //获取ServletContext对象，用于注册servlet
        ServletContext servletContext = config.getServletContext();

        //注册处理jsp的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        System.out.println("ConfigHelper.getAppJspPath() + \"*\"" + ConfigHelper.getAppJspPath() + "*");
        jspServlet.addMapping("/index.jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");

        //注册处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        System.out.println("ConfigHelper.getAppAssetPath() + \"*\"" + ConfigHelper.getAppAssetPath() + "*");
        defaultServlet.addMapping("/favicon.ico");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求方法与请求路径
        String requestMethod = req.getMethod().toLowerCase();
        String requestPath = req.getPathInfo();
        //resp.sendRedirect(req.getContextPath()+ /*ConfigHelper.getAppJspPath()+*/ "/test.jsp");
        /*req.getRequestDispatcher(ConfigHelper.getAppJspPath()+"test.jsp").forward(req, resp);

        return;*/
        //获取action处理器
        Handler handler = ControllerHelper.getHandle(requestMethod, requestPath);
        if(handler != null){
            //获取controller类和bean实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            //创建参数对象
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()){
                String paramName = paramNames.nextElement();
                String paramValue = req.getParameter(paramName);
                paramMap.put(paramName,paramValue);
            }
            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if(StringUtil.isNotEmpay(body)){
                String[] params = StringUtil.splitString(body, "&");
                if(ArrayUtil.isNotEmpty(params)){
                    for (String param : params){
                        String[] array = StringUtil.splitString(param, "=");
                        if (ArrayUtil.isNotEmpty(array) && array.length == 2){
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName, paramValue);
                        }
                    }
                }
            }
            Param param = new Param(paramMap);
            //调用action方法
            Method actionMethod = handler.getActionMethod();
            Object result;
            if (param.isEmpty()){
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
            } else{
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
            }
            //处理action方法返回值
            if(result instanceof View){
                //返回jsp页面
                View view = (View)result;
                String path = view.getPath();
                if(StringUtil.isNotEmpay(path)){
                    if(path.startsWith("/")){
                        resp.sendRedirect(req.getContextPath()+ path);
                    }else{
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry : model.entrySet()){
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        System.out.println("ConfigHelper.getAppJspPath() + path："+ ConfigHelper.getAppJspPath() + path);
                        System.out.println("req.getContextPath()+ path："+ req.getContextPath()+ path);

                        req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req, resp);
                    }
                }
            } else if(result instanceof Data){
                //返回json数据
                Data data = (Data) result;
                Object model = data.getModel();
                if(model != null){
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter writer = resp.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
        }
    }
}
