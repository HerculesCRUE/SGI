package org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * AutoConfiguration for {@link ApplicationContextSupport}.
 */
@Configuration
@ComponentScan(basePackageClasses = { ApplicationContextSupport.class })
public class ApplicationContextSupportAutoConfiguration {
}
