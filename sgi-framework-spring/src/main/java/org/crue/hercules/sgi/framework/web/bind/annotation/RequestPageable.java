package org.crue.hercules.sgi.framework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPageable {
  public static String DEFAULT_PAGE_HEADER = "X-Page";
  public static String DEFAIÑT_PAGE_SIZE_HEADER = "X-Page-Size";

  String pageHeader() default DEFAULT_PAGE_HEADER;

  String pageSizeHeader() default DEFAIÑT_PAGE_SIZE_HEADER;

  String sort() default "";

}