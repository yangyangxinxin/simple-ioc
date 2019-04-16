package link.yangxin.yestar.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author yangxin
 * @date 2019/4/16
 */
public class ConfigurationUtils {

    public static Map<String,Object> loadConfig(InputStream inputStream) throws IOException {
        Map<String, Object> configMap = new HashMap<>();
        Properties properties = new Properties();
        properties.load(inputStream);
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            Object nextElement = enumeration.nextElement();
            if (nextElement != null) {
                configMap.put(nextElement.toString(), properties.get(nextElement));
            }
        }
        IOUtils.closeQuietly(inputStream);
        return configMap;
    }

}