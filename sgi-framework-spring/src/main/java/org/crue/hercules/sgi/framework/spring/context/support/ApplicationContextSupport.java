package org.crue.hercules.sgi.framework.spring.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Allows access to Spring ApplicationContext in an static way.
 * <p>
 * This class is only usable in Spring Applications.
 */
@Component
public class ApplicationContextSupport implements ApplicationContextAware {
  private static final String MESSAGE_NOT_IN_APPLICATION_CONTEXT = "ApplicationContextSupport does not run in an ApplicationContext";
  private static final String MESSAGE_CODE_SUFFIX = ".message";

  private static ApplicationContext applicationContext;
  private static MessageSourceAccessor messageSourceAccessor;

  /*
   * Set the ApplicationContext that this object runs in. Normally this call will
   * be used to initialize the object. <p>Invoked after population of normal bean
   * properties but before an init callback such as {@link
   * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()} or a
   * custom init-method. Invoked after {@link
   * ResourceLoaderAware#setResourceLoader}, {@link
   * ApplicationEventPublisherAware#setApplicationEventPublisher} and {@link
   * MessageSourceAware}, if applicable.
   * 
   * @param applicationContext the ApplicationContext object to be used by this
   * object
   * 
   * @throws ApplicationContextException in case of context initialization errors
   * 
   * @throws BeansException if thrown by application context methods
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ApplicationContextSupport.setGlobalApplicationContext(applicationContext);
  }

  private static void setGlobalApplicationContext(ApplicationContext applicationContext) {
    ApplicationContextSupport.applicationContext = applicationContext;
    ApplicationContextSupport.messageSourceAccessor = new MessageSourceAccessor(applicationContext);
  }

  /**
   * Return the ApplicationContext that this object is associated with.
   * 
   * @return the ApplicationContext
   * @throws IllegalStateException if not running in an ApplicationContext
   */
  public static ApplicationContext getApplicationContext() throws IllegalStateException {
    if (applicationContext == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return applicationContext;
  }

  /**
   * Return the named Spring Bean from the ApplicationContext that this object is
   * associated with.
   * 
   * 
   * @param name the name of the bean to retrieve
   * @return an instance of the bean
   * @throws NoSuchBeanDefinitionException if there is no bean with the specified
   *                                       name
   * @throws BeansException                if the bean could not be obtained
   * @throws IllegalStateException         if not running in an ApplicationContext
   * @see BeanFactory
   */
  public static Object getBean(String name)
      throws NoSuchBeanDefinitionException, BeansException, IllegalStateException {
    return getApplicationContext().getBean(name);
  }

  /**
   * Return the typed Spring Bean from the ApplicationContext that this object is
   * associated with.
   * 
   * @param <T>   The expected type of the bean
   * @param clazz type the bean must match; can be an interface or superclass
   * @return an instance of the bean
   * @throws NoSuchBeanDefinitionException if there is no bean with the specified
   *                                       name
   * @throws BeansException                if the bean could not be obtained
   * @throws IllegalStateException         if not running in an ApplicationContext
   * @see BeanFactory
   */
  public static <T> T getBean(Class<T> clazz)
      throws NoSuchBeanDefinitionException, BeansException, IllegalStateException {
    return getApplicationContext().getBean(clazz);
  }

  /**
   * Return the named and typed Spring Bean from the ApplicationContext that this
   * object is associated with.
   * 
   * 
   * @param <T>   The expected type of the bean
   * @param name  the name of the bean to retrieve
   * @param clazz type the bean must match; can be an interface or superclass
   * @return an instance of the bean
   * @throws NoSuchBeanDefinitionException if there is no bean with the specified
   *                                       name
   * @throws BeansException                if the bean could not be obtained
   * @throws IllegalStateException         if not running in an ApplicationContext
   * @see BeanFactory
   */
  public static <T> T getBean(String name, Class<T> clazz)
      throws NoSuchBeanDefinitionException, BeansException, IllegalStateException {
    return getApplicationContext().getBean(name, clazz);
  }

  /**
   * Return a MessageSourceAccessor for the application context used by this
   * object, for easy message access.
   * 
   * @return the MessageSourceAccessor for the ApplicationContext
   * @throws IllegalStateException if not running in an ApplicationContext
   */
  public static MessageSourceAccessor getMessageSourceAccessor() throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor;
  }

  /**
   * Retrieve the message for the given code and the default Locale.
   * 
   * @param code the code of the message
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(String code) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(code);
  }

  /**
   * Retrieve the message for the given code and the default Locale.
   * 
   * @param code the code of the message
   * @param args arguments for the message, or {@code null} if none
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(String code, Object... args) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(code, args);
  }

  /**
   * Retrieve the message for the class (using the class name concatenated with
   * `.message` as code) and the default Locale.
   * 
   * @param clazz the class of the message code
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(Class<?> clazz) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(clazz.getName() + MESSAGE_CODE_SUFFIX);
  }

  /**
   * Retrieve the message for the given class (using the class name concatenated
   * with `.message` as code) and the default Locale.
   * 
   * @param clazz the class of the message code
   * @param args  arguments for the message, or {@code null} if none
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(Class<?> clazz, Object... args) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(clazz.getName() + MESSAGE_CODE_SUFFIX, args);
  }

  /**
   * Retrieve the message for the given class (using the class name concatenated
   * with property and `.message` as code) and the default Locale.
   * 
   * @param clazz    the class of the message code
   * @param property the property of the message code
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(Class<?> clazz, String property) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(clazz.getName() + "." + property + MESSAGE_CODE_SUFFIX);
  }

  /**
   * Retrieve the message for the given class (using the class name concatenated
   * with property and `.message` as code) and the default Locale.
   * 
   * @param clazz    the class of the message code
   * @param property the property of the message code
   * @param args     arguments for the message, or {@code null} if none
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(Class<?> clazz, String property, Object... args) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(clazz.getName() + "." + property + MESSAGE_CODE_SUFFIX, args);
  }

  /**
   * Retrieve the given MessageSourceResolvable (e.g. an ObjectError instance) in
   * the default Locale.
   * 
   * @param resolvable the MessageSourceResolvable
   * @return the message
   * @throws org.springframework.context.NoSuchMessageException if not found
   * @throws IllegalStateException                              if the application
   *                                                            is not run in an
   *                                                            ApplicationContext
   */
  public static String getMessage(MessageSourceResolvable resolvable) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException(MESSAGE_NOT_IN_APPLICATION_CONTEXT);
    }
    return messageSourceAccessor.getMessage(resolvable);
  }
}
