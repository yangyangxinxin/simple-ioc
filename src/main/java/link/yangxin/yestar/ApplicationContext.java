package link.yangxin.yestar;

import link.yangxin.yestar.util.ClassLoaderUtil;
import link.yangxin.yestar.util.ConfigurationUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangxin
 * @date 2019/4/16
 */
public class ApplicationContext {

    private String configPath;

    private Set<String> classSet;

    private Map<String, Object> iocContext;

    public Object getBean(String name) {
        return iocContext.get(name);
    }

    public ApplicationContext(String configPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        this.configPath = configPath;
        this.classSet = new HashSet<>();
        this.iocContext = new ConcurrentHashMap<>();
        this.initContext();
    }

    public void initContext() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Map<String, Object> configMap = ConfigurationUtils.loadConfig(this.getClass().getClassLoader().getResourceAsStream(configPath));
        String scanPath = (String) configMap.get("annotation.base.scan");
        ClassLoaderUtil.getPackageClassFile(scanPath.replaceAll("\\.", "/"), classSet);

        for (String className : classSet) {
            ClassLoaderUtil.addBeanToIoc(Class.forName(className), iocContext);
        }

        // 此时iocContext中的bean都已经实例化成功，但是属性还没有初始化
        for (String beanName : iocContext.keySet()) {
            ClassLoaderUtil.autowiredObject(iocContext.get(beanName), iocContext, configMap);
        }
    }

}