package org.crue.hercules.sgi.framework.web.config;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.http.converter.json.PageMappingJackson2HttpMessageConverter;
import org.crue.hercules.sgi.framework.web.method.annotation.RequestPageableArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * Defines callback methods to customize the Java-based configuration for Spring
 * MVC enabled via {@code @EnableWebMvc}.
 */
@Component
// If you add @EnableWebMvc Spring Boot's autoconfiguration is disabled
@Slf4j
public class SgiWebConfig implements WebMvcConfigurer {
  private static SortCriteriaConverter sortOperationConverter = new SortCriteriaConverter();
  private static RequestPageableArgumentResolver requestPageableArgumentResolver = new RequestPageableArgumentResolver(
      sortOperationConverter);

  /**
   * Configure cross origin requests processing.
   * 
   * @param registry {@link CorsRegistry}
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    log.debug("addCorsMappings(CorsRegistry registry) - start");
    registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH").exposedHeaders(
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_SIZE_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_COUNT_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_TOTAL_COUNT_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_TOTAL_COUNT_HEADER);
    log.debug("addCorsMappings(CorsRegistry registry) - end");
  }

  /**
   * Modifies the {@link HttpMessageConverter HttpMessageConverters} to use for
   * reading or writing to the body of the request or response. If a
   * MappingJackson2HttpMessageConverter is found, it is replaced with the
   * PageMappingJackson2HttpMessageConverter.
   * 
   * @param converters the list of converters
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    log.debug("configureMessageConverters(List<HttpMessageConverter<?>> converters) - start");
    for (HttpMessageConverter<?> httpMessageConverter : converters) {
      if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
        // Override all MappingJackson2HttpMessageConverter with custom
        // PageMappingJackson2HttpMessageConverter
        // One is created by WebMvcConfigurationSupport
        // One is created by AllEncompassingFormHttpMessageConverter
        MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) httpMessageConverter;
        PageMappingJackson2HttpMessageConverter newConverter = pageMappingJackson2HttpMessageConverter(
            converter.getObjectMapper());
        converters.set(converters.indexOf(converter), newConverter);
      }
    }
    log.debug("configureMessageConverters(List<HttpMessageConverter<?>> converters) - end");
  }

  /**
   * Add {@link Converter Converters} and {@link Formatter Formatters} in addition
   * to the ones registered by default.
   *
   * @param registry {@link FormatterRegistry}
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    log.debug("addFormatters(FormatterRegistry registry) - start");
    registry.addConverter(sortOperationConverter);
    log.debug("addFormatters(FormatterRegistry registry) - end");
  }

  /**
   * Add resolvers to support custom controller method argument types.
   * <p>
   * This does not override the built-in support for resolving handler method
   * arguments. To customize the built-in support for argument resolution,
   * configure {@link RequestMappingHandlerAdapter} directly.
   * 
   * @param resolvers initially an empty list
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    log.debug("addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) - start");
    resolvers.add(requestPageableArgumentResolver);
    log.debug("addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) - end");
  }

  /**
   * Custom {@link MappingJackson2HttpMessageConverter} that handles paging
   * information using Http headers.
   * 
   * @param objectMapper the {@link ObjectMapper} to use
   * @return the {@link PageMappingJackson2HttpMessageConverter}
   */
  @Bean
  public PageMappingJackson2HttpMessageConverter pageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    log.debug("pageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) - start");
    PageMappingJackson2HttpMessageConverter returnValue = new PageMappingJackson2HttpMessageConverter(objectMapper);
    log.debug("pageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) - end");
    return returnValue;
  }
}