package org.leave.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className, boolean isInitialized){
        Class<?> cls;
        try{
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e){
            LOGGER.error("Load class failure", e);
            throw new RuntimeException();
        }
        return cls;
    }

    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try{
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                LOGGER.debug("url:" + url.toString());
                if(url != null){
                    String protocal = url.getProtocol();
                    if(protocal.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20", "");
                        LOGGER.debug("packagePath:" + packagePath);
                        addClass(classSet, packagePath, packageName);
                    } else if(protocal.equals("jar")){
                        JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                        if(jarURLConnection != null){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            LOGGER.debug("jarFile name:" + jarFile.getName());
                            if(jarFile != null){
                                Enumeration<JarEntry> jarEnttries = jarFile.entries();
                                while (jarEnttries.hasMoreElements()){
                                    JarEntry jarEntry = jarEnttries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    LOGGER.debug("jarEntryName:" + jarEntryName);
                                    if(jarEntryName.endsWith(".class")){
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        LOGGER.debug("class name in the jar:" + className);
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            LOGGER.error("Get class set failure", e);
            throw new RuntimeException();
        }

        return classSet;
    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName){
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                LOGGER.debug("file name:" + file.getName());
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        LOGGER.debug("iterartor files");
        for (File file : files){
            String fileName = file.getName();
            LOGGER.debug("file name:" + file.getName());
            if(file.isFile()){
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if(StringUtil.isNotEmpay(packageName)){
                    className = packageName + className;
                    LOGGER.debug("className:" + className);
                }
                doAddClass(classSet, className);
            } else{
                String subPackagePath = fileName;
                if(StringUtil.isNotEmpay(packagePath)){
                    subPackagePath = packagePath + "/" + subPackagePath;
                    LOGGER.debug("subPackagePath:" + subPackagePath);
                }
                String subPackageName = fileName;
                if(StringUtil.isNotEmpay(packageName)){
                    subPackageName = packageName + "." + subPackageName;
                    LOGGER.debug("subPackageName:" + subPackageName);
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classes, String className){
        Class<?> cls = loadClass(className, false);
            classes.add(cls);
    }

}
