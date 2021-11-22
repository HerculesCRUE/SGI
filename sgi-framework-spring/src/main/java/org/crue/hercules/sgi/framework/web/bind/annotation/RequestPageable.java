package org.crue.hercules.sgi.framework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Resolves method parameters into argument values in the context of a given
 * request.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPageable {
  /**
   * Default Http Header containing the page number.
   */
  public static String DEFAULT_PAGE_HEADER = "X-Page";
  /**
   * Default Http Header containing the page size.
   */
  public static String DEFAULT_PAGE_SIZE_HEADER = "X-Page-Size";

  /**
   * The Http Header containing the page number.
   * 
   * @return the Http Header name containing the page number
   */
  String pageHeader() default DEFAULT_PAGE_HEADER;

  /**
   * The Http Header containing the page size.
   * 
   * @return the Http Header name containing the page size
   */
  String pageSizeHeader() default DEFAULT_PAGE_SIZE_HEADER;

  /**
   * Request parameter name containing sort information.
   * 
   * @return the Request parameter name containing sort information
   */
  String sort() default "";

}