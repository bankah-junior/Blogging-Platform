package com.amalitech.SpringBootBloggingApp.aspects;

import com.amalitech.SpringBootBloggingApp.service.MetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final MetricsService metricsService;

    public PerformanceAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Pointcut("execution(* com.amalitech.SpringBootBloggingApp.service.impl.*.*(..))")
    public void serviceImplMethods() {}

    @Around("serviceImplMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getDeclaringTypeName()
                + "." + pjp.getSignature().getName();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("Method {} took {} ms", methodName, elapsed);
            metricsService.record(methodName, elapsed);
            return result;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("Method {} threw {} after {} ms", methodName, t.getClass().getSimpleName(), elapsed);
            metricsService.recordError(methodName, elapsed);
            throw t;
        }
    }
}
