package org.crue.hercules.sgi.framework.problem.message;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

class MessageInterpolator {
  private static javax.validation.MessageInterpolator interpolator;

  private MessageInterpolator() {
  }

  public static String interpolateMessage(@Nullable final String message,
      @Nullable Map<String, Object> messageParameters) {
    if (message == null) {
      return null;
    }
    javax.validation.MessageInterpolator messageInterpolator = getMessageInterpolator();
    ConstraintDescriptor<?> constraintDescriptor = null;
    Object validatedValue = null;
    Class<?> rootBeanType = null;
    Path propertyPath = null;
    messageParameters = messageParameters != null ? messageParameters : Collections.emptyMap();

    Map<String, Object> expressionVariables = Collections.emptyMap();
    MessageInterpolatorContext messageInterpolatorContext = new MessageInterpolatorContext(constraintDescriptor,
        validatedValue, rootBeanType, propertyPath, messageParameters, expressionVariables);
    return messageInterpolator.interpolate(message, messageInterpolatorContext, getLocale());
  }

  private static javax.validation.MessageInterpolator getMessageInterpolator() {
    // If already defined
    if (interpolator != null) {
      return interpolator;
    }
    // If not defined, create a new one from ProblemMessages.properties if exists in
    // the classpath
    interpolator = buildInterpolatorFromProblemMessages();
    return interpolator;
  }

  private static javax.validation.MessageInterpolator buildInterpolatorFromProblemMessages() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("ProblemMessages", "org/crue/hercules/sgi/framework/problem/ProblemMessages");
    return new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource));
  }

  private static Locale getLocale() {
    return LocaleContextHolder.getLocale();
  }

}
