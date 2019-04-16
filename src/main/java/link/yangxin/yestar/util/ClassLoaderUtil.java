package link.yangxin.yestar.util;

import link.yangxin.yestar.annotation.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yangxin
 * @date 2019/4/16
 */
public class ClassLoaderUtil {

    /**
     * 扫码包下的文件
     *
     * @param packagePath
     * @param classSet
     */
    public static void getPackageClassFile(String packagePath, Set<String> classSet) {
        URL url = ClassLoaderUtil.class.getClassLoader().getResource(packagePath);
        File file = new File(url.getFile());
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File childFile : files) {
                if (childFile.isDirectory()) {
                    // 如果是文件夹，递归扫描文件夹下的文件
                    getPackageClassFile(packagePath + "/" + childFile.getName(), classSet);
                } else {
                    // 是文件并且是以 .class结尾
                    if (childFile.getName().endsWith(".class")) {
                        System.out.println("正在加载: " + packagePath.replace("/", ".") + "." + childFile.getName());
                        classSet.add(packagePath.replace("/", ".") + "." + childFile.getName().replace(".class", ""));
                    }
                }
            }
        } else {
            throw new RuntimeException("该路径不存在！");
        }
    }

    /**
     * 将bean添加到ioc容器中.
     *
     * @param clazz
     * @param iocContext {beanName:beanObj}
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addBeanToIoc(Class clazz, Map<String, Object> iocContext) throws IllegalAccessException, InstantiationException {
        if (clazz.getAnnotation(Service.class) != null) {
            Service service = (Service) clazz.getAnnotation(Service.class);
            if (StringUtils.isNoneBlank(service.value())) {
                iocContext.put(toLowercaseIndex(service.value()), clazz.newInstance());
            } else {
                iocContext.put(toLowercaseIndex(clazz.getSimpleName()), clazz.newInstance());
            }
            System.out.println("控制反转访问Service对象:" + toLowercaseIndex(clazz.getSimpleName()));
        }
        if (clazz.getAnnotation(Controller.class) != null) {
            iocContext.put(toLowercaseIndex(clazz.getSimpleName()), clazz.newInstance());
            System.out.println("控制反转访问Controller对象:" + toLowercaseIndex(clazz.getSimpleName()));
        }
        if (clazz.getAnnotation(Repository.class) != null) {
            Repository repository = (Repository) clazz.getAnnotation(Repository.class);
            if (StringUtils.isNoneBlank(repository.value())) {
                iocContext.put(toLowercaseIndex(repository.value()), clazz.newInstance());
            } else {
                iocContext.put(toLowercaseIndex(clazz.getSimpleName()), clazz.newInstance());
            }
            System.out.println("控制反转访问Repository对象:" + toLowercaseIndex(clazz.getSimpleName()));
        }
    }

    /**
     * 自动注入到对象中
     *
     * @param object     要注入的对象
     * @param iocContext ioc bean容器
     */
    public static void autowiredObject(Object object, Map<String, Object> iocContext,Map<String,Object> configContext) throws IllegalAccessException, InstantiationException {
        Class<?> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            AutoWired autoWiredAnnotation = field.getAnnotation(AutoWired.class);
            Value valueAnnotation = field.getAnnotation(Value.class);
            // 处理autowired注解
            if (autoWiredAnnotation != null) {
                if (field.getType().isInterface()) { //  接口不能实例化，只能去找他的实现类
                    if (StringUtils.isNotBlank(autoWiredAnnotation.value())) {
                        // 如果注解指定了对象的bean 名字，就直接从ioc容器中取
                        field.set(object, iocContext.get(autoWiredAnnotation.value()));
                    } else {
                        // 字段名个接口名一致，直接从容器取
                        Object bean = iocContext.get(field.getName());
                        if (bean != null) {
                            field.set(object, bean);
                            //autowiredObject(field.getType(), iocContext,configContext);
                        } else {
                            List<Object> interfaces = findSuperClassFromIoc(field.getType(), iocContext);
                            if (interfaces != null && !interfaces.isEmpty()) {
                                if (interfaces.size() > 1) {
                                    throw new RuntimeException("当前类" + object.getClass() + "不能注入接口：" + field.getName() + ",ioc容器中有多个该接口的实现类，请指定beanName进行注入！");
                                }
                                field.set(object, interfaces.get(0));
                                // 递归依赖注入
                                //autowiredObject(field.getType(), iocContext,configContext);
                            } else {
                                throw new RuntimeException("当前类" + object.getClass() + "不能注入接口：" + field.getName() + ",ioc容器中没有该类的实现类！");
                            }
                        }
                    }
                } else {
                    String beanName = StringUtils.isBlank(autoWiredAnnotation.value()) ? toLowercaseIndex(field.getName()) : toLowercaseIndex(autoWiredAnnotation.value());
                    Object bean = iocContext.get(beanName);
                    field.set(object, bean == null ? field.getType().newInstance() : bean);
                    System.out.println("依赖注入" + field.getName());
                }
                // 递归依赖注入
                //autowiredObject(field.getType(), iocContext,configContext);
            }
            if (valueAnnotation != null) {
                field.setAccessible(true);
                field.set(object, StringUtils.isNotEmpty(valueAnnotation.value()) ? configContext.get(valueAnnotation.value()) : null);
                System.out.println("注入配置文件  " + object.getClass() + " 加载配置属性" + valueAnnotation.value());
            }
        }
    }

    /**
     * 类名首字母转小写
     */
    public static String toLowercaseIndex(String name) {
        if (StringUtils.isNotEmpty(name)) {
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        return name;
    }

    /**
     * 从ioc容器中 找到接口的实现类
     *
     * @param clazz
     * @param iocContext
     * @return
     */
    public static List<Object> findSuperClassFromIoc(Class clazz, Map<String, Object> iocContext) {
        List<Object> list = new ArrayList<>();
        Set<String> beanNameList = iocContext.keySet();
        beanNameList.forEach(key -> {
            Object obj = iocContext.get(key);
            Class<?>[] interfaces = obj.getClass().getInterfaces();
            if (ArrayUtils.contains(interfaces, clazz)) {
                list.add(obj);
            }
        });
        return list;
    }

}