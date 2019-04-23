package com.ydg.cloud.lock.annotation;

import com.ydg.cloud.lock.enums.LockLevelEnum;
import com.ydg.cloud.lock.enums.SourceTypeEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YDG
 * @Company qcsz
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
     *
     * @return
     */
    SourceTypeEnum lockTypeEnum() default SourceTypeEnum.CUSTOMIZE;

}
