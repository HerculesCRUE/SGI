package org.crue.hercules.sgi.tp.run;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.tp.exceptions.RunnableBeanMethodException;
import org.springframework.util.ReflectionUtils;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled task operation class
 */
@EqualsAndHashCode
@Slf4j
public class RunnableBeanMethod implements Runnable {

  private String beanName;

  private String methodName;

  private Object[] params;

  public RunnableBeanMethod(Class<?> beanClass, Method method) {
    this(beanClass, method, (Object[]) null);
  }

  public RunnableBeanMethod(Class<?> beanClass, String methodName) {
    this(beanClass, methodName, (Object[]) null);
  }

  public RunnableBeanMethod(Class<?> beanClass, Method method, Object... params) {
    String[] beanNames = ApplicationContextSupport.getApplicationContext().getBeanNamesForType(beanClass);
    if (beanNames.length != 1) {
      throw new RunnableBeanMethodException(
          String.format("RunnableBeanMethod required a single bean of type %s, but %d were found", beanClass.getName(),
              beanNames.length));
    }
    this.beanName = beanNames[0];
    this.methodName = method.getName();
    this.params = params;
  }

  public RunnableBeanMethod(Class<?> beanClass, String methodName, Object... params) {
    String[] beanNames = ApplicationContextSupport.getApplicationContext().getBeanNamesForType(beanClass);
    if (beanNames.length != 1) {
      throw new RunnableBeanMethodException(
          String.format("RunnableBeanMethod required a single bean of type %s, but %d were found", beanClass.getName(),
              beanNames.length));
    }
    this.beanName = beanNames[0];
    this.methodName = methodName;
    this.params = params;
  }

  public RunnableBeanMethod(String beanName, String methodName) {
    this(beanName, methodName, (Object[]) null);
  }

  public RunnableBeanMethod(String beanName, String methodName, Object... params) {
    this.beanName = beanName;
    this.methodName = methodName;
    this.params = params;
  }

  @Override
  public void run() {
    log.debug("run() - start");

    try {
      Object target = ApplicationContextSupport.getBean(beanName);

      Method method = null;
      if (null != params && params.length > 0) {
        Class<?>[] paramCls = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
          paramCls[i] = params[i].getClass();
        }
        method = target.getClass().getDeclaredMethod(methodName, paramCls);
      } else {
        method = target.getClass().getDeclaredMethod(methodName);
      }

      ReflectionUtils.makeAccessible(method);
      if (null != params && params.length > 0) {
        method.invoke(target, params);
      } else {
        method.invoke(target);
      }
    } catch (Exception ex) {
      log.error(String.format("Execution exception - bean: %s，Method: %s，Parameters: %s ", beanName, methodName,
          Arrays.toString(params)), ex);
    }

    log.debug("run() - end");
  }

  public String getBeanName() {
    return beanName;
  }

  public String getMethodName() {
    return methodName;
  }

  public Object[] getParams() {
    return params;
  }

}