
package io.github.ceragon.util;


import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描工具类
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ClassUtil {
    private final Logger log;
    private final ClassLoader classLoader;

    public ClassUtil(Logger log, ClassLoader classLoader) {
        this.log = log;
        this.classLoader = classLoader;
    }

    public ClassUtil(Logger log, List<String> compilePath) throws MalformedURLException {
        this.log = log;
        this.classLoader = getClassLoader(compilePath);
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return this.classLoader.loadClass(className);
    }

    public static ClassLoader getClassLoader(List<String> compilePath) throws MalformedURLException {
        // 转为 URL 数组
        URL[] urls = new URL[compilePath.size()];
        for (int i = 0; i < compilePath.size(); ++i) {
            urls[i] = new File(compilePath.get(i)).toPath().toUri().toURL();
        }
        // 自定义类加载器
        return new URLClassLoader(urls, ClassUtil.class.getClassLoader());

    }

    /**
     * 按类使用的注解扫描
     *
     * @param basePackagesStr 需要扫描的包，多个包使用“,”分隔
     * @param annClazz        需要扫描的类必须使用的Runtime级别注解
     * @return
     */
    public Set<Class<?>> scan(String basePackagesStr, Class<?> annClazz) {
        log.info("Scan start");
        URL url;
        String protocol;
        String filePath;
        String packageName;
        String packagePath;
        Enumeration<URL> list;
        Set<Class<?>> classes = new HashSet<>();
        if (basePackagesStr == null) return classes;
        String[] basePackages = basePackagesStr.split(",");
        for (String basePackage : basePackages) {
            packageName = basePackage;
            if (packageName.endsWith(".")) {
                packageName = packageName
                        .substring(0, packageName.lastIndexOf('.'));
            }
            packagePath = packageName.replace('.', '/');
            try {
                list = classLoader.getResources(packagePath);
                while (list.hasMoreElements()) {
                    url = list.nextElement();
                    protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                        Set<Class<?>> value = scanFiles(packageName, filePath, annClazz);
                        if (value != null) {
                            classes.addAll(value);
                        }
                    } else if ("jar".equals(protocol)) {
                        classes.addAll(scanJar(packageName, url, annClazz));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    /**
     * 扫描并加载目录中的Class文件
     *
     * @param packagePath 要扫描的包，如：com.server_game.model
     * @param filePath    要扫描的Class文件路径，与包参数对应，如：file://D:/Common/bin/com/server_game/model
     * @param annClazz    扫描的Class需使用的Runtime注解
     * @return
     */
    private Set<Class<?>> scanFiles(String packagePath, String filePath, Class annClazz) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        File[] dirfiles = dir.listFiles();
        if (dirfiles == null) {
            return null;
        }
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                Set<Class<?>> value = scanFiles(packagePath + "." + file.getName(), file.getAbsolutePath(), annClazz);
                if (value != null) {
                    classes.addAll(value);
                }
            } else {
                if (!file.getPath().endsWith("class")) continue;
                String className = file.getName().substring(0, file.getName().length() - 6);
                className = packagePath + '.' + className;
                try {
                    log.info("start load:" + className);
                    Class clazz = classLoader.loadClass(className);
                    log.info("load finish:" + clazz);
                    if (annClazz != null) {
                        Annotation ann = clazz.getAnnotation(annClazz);
                        if (ann != null) {
                            classes.add(clazz);
                        }
                    } else {
                        classes.add(clazz);
                    }

                } catch (Exception e) {
                    if (log.isErrorEnabled()) log.error("加载类<" + className + ">出错", e);
                }
            }
        }
        return classes;
    }

    /**
     * 扫描并加载JAR中的Class文件
     *
     * @param packagePath 要扫描的包，如：com.server_game.model
     * @param url         要扫描的Jar文件路径，如：jar:file://D:/GameServer/lib/Common.jar
     * @param annClazz    扫描的Class需使用的Runtime注解
     * @return
     */
    private Set<Class<?>> scanJar(String packagePath, URL url, Class annClazz) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        packagePath = packagePath.replace('.', '/');
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(packagePath) || entry.isDirectory()) {
                    continue;
                }

                String className = name.replace('/', '.');
                className = className.substring(0, className.length() - 6);
                try {
                    Class clazz = classLoader.loadClass(className);
                    if (annClazz != null) {
                        Annotation ann = clazz.getAnnotation(annClazz);
                        if (ann != null) {
                            classes.add(clazz);
                        }
                    } else {
                        classes.add(clazz);
                    }
                } catch (Exception e) {
                    if (log.isErrorEnabled()) log.error("从包<" + url + ">加载类<" + className + ">出错", e);
                }
            }
        } catch (IOException e) {
            if (log.isErrorEnabled()) log.error("读取包<" + url + ">出错", e);
        }
        return classes;
    }
}
