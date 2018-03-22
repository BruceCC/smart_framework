package org.leave.framework;

import org.leave.framework.bean.Data;
import org.leave.framework.bean.Handler;
import org.leave.framework.bean.Param;
import org.leave.framework.bean.View;
import org.leave.framework.helper.*;
import org.leave.framework.util.JsonUtil;
import org.leave.framework.util.ReflectionUtil;
import org.leave.framework.util.StringUtil;

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
        //jspServlet.addMapping(ConfigHelper.getAppJspPath() + "common/*");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");

        //注册处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        System.out.println("ConfigHelper.getAppAssetPath() + \"*\"" + ConfigHelper.getAppAssetPath() + "*");
        defaultServlet.addMapping("/favicon.ico");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");

        UploadHelper.init(servletContext);

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletHelper.init(req, resp);

        try{
            //获取请求方法与请求路径
            String requestMethod = req.getMethod().toLowerCase();
            String requestPath = req.getPathInfo();

            if (requestPath.equals("/favico.ico")){
                return;
            }

            //获取action处理器
            Handler handler = ControllerHelper.getHandle(requestMethod, requestPath);
            if(handler != null){
                //获取controller类和bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                //创建参数对象
                Param param;
                if (UploadHelper.isMultipart(req)){
                    param = UploadHelper.createParam(req);
                } else{
                    param = RequestHelper.createParam(req);
                }
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
                    handleViewResult(view, req, resp);
                } else if(result instanceof Data){
                    //返回json数据
                    Data data = (Data) result;
                    handleDataResult(data, resp);
                }
            }
        } finally {
            ServletHelper.destory();
        }
    }

    private void handleViewResult(View view, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String path = view.getPath();
        if(StringUtil.isNotEmpay(path)){
            if(path.startsWith("/")){
                resp.sendRedirect(req.getContextPath()+ path);
            }else{
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()){
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                //System.out.println("ConfigHelper.getAppJspPath() + path："+ ConfigHelper.getAppJspPath() + path);
                //System.out.println("req.getContextPath()+ path："+ req.getContextPath()+ path);

                req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req, resp);
            }
        }
    }

    private void handleDataResult(Data data, HttpServletResponse resp) throws ServletException, IOException{
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
