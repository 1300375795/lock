package com.ydg.cloud.lock.annotation;

import com.ydg.cloud.lock.constants.Constant;
import com.ydg.cloud.lock.enums.LockLevelEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁实现方式
     *
     * @return
     */
    LockLevelEnum type() default LockLevelEnum.REDIS;

    /**
     * key名称，从方法入参中获取指定的key（支持复杂类型）
     * 用法:
     * 如果你要以xxDTO的id作为key进行锁定资源,那么就是如下写法
     * keyName = "xxDTO.id"
     *
     * @return
     */
    String keyName();

    /**
     * 资源类型
     * <p>
     * 基于这个资源类型进行拼接key
     * 每个应用需要自定定义资源类型
     * 资源类型需要加上应用的前缀
     * 以防止资源在不同的应用都存在的时候得到一样的key
     *
     * @return
     */
    String sourceType() default Constant.CUSTOMIZE_SOURCE_TYPE;

    /**
     * 最大尝试获取锁时间--毫秒
     *
     * @return
     */
    long maxTryLockTimeOut() default 10000L;

}
