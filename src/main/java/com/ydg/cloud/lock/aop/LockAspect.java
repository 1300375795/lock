package com.ydg.cloud.lock.aop;

import com.ydg.cloud.lock.annotation.Lock;
import com.ydg.cloud.lock.enums.LockLevelEnum;
import com.ydg.cloud.lock.exception.LockException;
import com.ydg.cloud.lock.lock.AbstractLock;
import com.ydg.cloud.lock.utils.Validator;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Slf4j
@Aspect
@Component
public class LockAspect {

    /**
     * 对象属性，连接符
     */
    private static final String POINT = ".";

    /**
     * 最顶层基类名字： java.lang.Object
     */
    private static final String TOP_CLASS_NAME = "java.lang.Object";

    @Autowired
    private ApplicationContext context;

    @Around("@annotation(annotation)")
    public Object lock(ProceedingJoinPoint joinPoint, Lock annotation) throws Throwable {
        //获取key
        String keyName = annotation.keyName();
        String key = this.getKey(joinPoint, keyName);
        //锁实现
        LockLevelEnum type = annotation.type();
        //资源类型
        String sourceType = annotation.sourceType();

        AbstractLock lock = context.getBean(type.getClz());
        lock.setKey(key);
        lock.setSourceType(sourceType);
        lock.setMaxTryLockTimeOut(annotation.maxTryLockTimeOut());
        lock.lock();
        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * 获取key
     *
     * @param joinPoint
     * @param keyName
     * @return
     * @throws IllegalAccessException
     */
    private String getKey(ProceedingJoinPoint joinPoint, String keyName) throws IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        Validator.isTrue(args != null && args.length > 0, new LockException("annotation Lock method have not params"));
        Validator.isTrue(StringUtils.isNotBlank(keyName),
                new LockException("annotation Lock keyName can't be null or empty"));
        //匹配到对应参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        //获取参数名
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        String key;
        //获取参数所在索引
        int index = -1;
        int pointIndex = keyName.indexOf(POINT);
        //基于POINT判断需要被lock的属性是一个对象的某个属性类型还是就是一个基础类型
        String matchName = pointIndex == -1 ? keyName : keyName.substring(0, pointIndex);
        //基于得到的属性名称跟给出的参数名称进行名称对比,如果匹配到了拿到这个index
        for (int i = 0; i < parameterNames.length; i++) {
            if (matchName.equals(parameterNames[i])) {
                index = i;
                break;
            }
        }
        Validator.isTrue(index != -1, new LockException("annotation Lock keyName can not match methodParameter"));
        Object arg = args[index];
        Validator.notNull(arg, new LockException("annotation Lock keyName match methodParameter is null"));
        if (pointIndex == -1) {
            //基础类型，直接把值赋给对应参数
            key = arg.toString();
        } else {
            //复杂对象类型，赋值到对象的属性当中去
            String fieldName = keyName.substring(pointIndex + 1);
            Field field = getField(arg.getClass(), fieldName);
            Validator.isTrue(field != null, new LockException("annotation Lock keyName can not match methodParameter"));
            field.setAccessible(true);
            Object fieldValue = field.get(arg);
            Validator.notNull(fieldValue, new LockException("annotation Lock keyName match methodParameter is null"));
            key = fieldValue.toString();
        }
        Validator.isTrue(StringUtils.isNotBlank(key), new LockException("Lock key must be null or empty"));
        return key;
    }

    /**
     * 根据class名称和字段名称，获取到对应的field，支持父类属性
     *
     * @param clz
     * @param fieldName
     * @return
     */
    private Field getField(Class<?> clz, String fieldName) {
        if (clz.getName().equals(TOP_CLASS_NAME)) {
            return null;
        }

        Field[] declaredFields = clz.getDeclaredFields();
        Field matchField = null;
        for (Field field : declaredFields) {
            if (fieldName.equals(field.getName())) {
                matchField = field;
                break;
            }
        }

        return matchField != null ? matchField : getField(clz.getSuperclass(), fieldName);
    }

}
