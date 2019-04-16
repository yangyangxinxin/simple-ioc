package link.yangxin.yestar.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记service
 * @author yangxin
 * @date 2019/4/16
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {

    String value() default "";

}
