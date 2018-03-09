package com.pxene.odin.cloud.web.aspect;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 使用AOP统一处理Web请求日志，日志切面：Controller和Service。
 * 输出指标包括方法的参数名、参数值，方法执行时间。
 * @author ningyu
 */
@Component
@Aspect
@Slf4j
public class WebLogAspect
{
    private static final LocalVariableTableParameterNameDiscoverer DISCOVERER = new LocalVariableTableParameterNameDiscoverer();
    private static final String PARAM_TEMPLATE = "{0}({1}) : {2}";


    @Pointcut("execution(* com.pxene.odin.cloud.web.*.*Controller.*(..))")
    public void executeController()
    {
    }

    @Pointcut("execution(* com.pxene.odin.cloud.service.*Service.*(..))")
    public void executeService()
    {
    }

    @Pointcut("executeController() || executeService()")
    public void executeControllerOrService()
    {
    }

    @Around(value = "executeControllerOrService()")
    public Object recordRequestParamsAndResult(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Long startTime = System.currentTimeMillis();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        Method method = methodSignature.getMethod();

        String methodName = methodSignature.toShortString();

        String[] paramsNames = DISCOVERER.getParameterNames(method);
        Object[] paramsValues = joinPoint.getArgs();

        StringBuilder paramsBuilder = new StringBuilder("### Odin-Cloud ### [Details for method '" + methodName + "' received request params] -> ");
        StringBuilder resultBuilder = new StringBuilder("### Odin-Cloud ### [Details for method '" + methodName + "' return result] -> ");

        for (int i = 0; i < paramsNames.length; i++)
        {
            String paramName = paramsNames[i];
            Object paramValue = paramsValues[i];
            String paramType = (paramValue == null) ? null : paramValue.getClass().getSimpleName();
            String paramStr = MessageFormat.format(PARAM_TEMPLATE, paramName, paramType, paramValue);
            paramsBuilder.append(paramStr);

            if (i < paramsNames.length - 1)
            {
                paramsBuilder.append(", ");
            }
        }

        Object result = joinPoint.proceed();
        resultBuilder.append(result);

        // 打印方法入参
        log.debug(paramsBuilder.toString());

        // 打印方法返回值
        log.debug(resultBuilder.toString());

        // 打印方法执行时间
        log.debug("### Odin-Cloud ### Method '{}' cost '{}' micro-seconds totally.", methodName, System.currentTimeMillis() - startTime);

        return result;
    }
}
