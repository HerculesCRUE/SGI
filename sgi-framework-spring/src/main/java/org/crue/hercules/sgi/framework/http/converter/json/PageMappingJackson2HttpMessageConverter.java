package org.crue.hercules.sgi.framework.http.converter.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of
 * {@link org.springframework.http.converter.HttpMessageConverter} that can read
 * and write JSON using <a href="https://github.com/FasterXML/jackson">Jackson
 * 2.x's</a> {@link ObjectMapper}.
 *
 * <p>
 * This converter can be used to bind to typed beans, or untyped {@code HashMap}
 * instances.
 *
 * <p>
 * By default, this converter supports {@code application/json} and
 * {@code application/*+json} with {@code UTF-8} character set. This can be
 * overridden by setting the {@link #setSupportedMediaTypes supportedMediaTypes}
 * property.
 *
 * <p>
 * The default constructor uses the default configuration provided by
 * {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>
 * Compatible with Jackson 2.9 and higher, as of Spring 5.0.
 */
@Slf4j
public class PageMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
  /**
   * Default Http Header for the page number.
   */
  public static final String DEFAULT_PAGE_HEADER = "X-Page";
  /**
   * Default Http Header for the page size.
   */
  public static final String DEFAULT_PAGE_SIZE_HEADER = "X-Page-Size";
  /**
   * Default Http Header for the number of pages.
   */
  public static final String DEFAULT_PAGE_COUNT_HEADER = "X-Page-Count";
  /**
   * Default Http Header for the total number of elements in the page.
   */
  public static final String DEFAULT_PAGE_TOTAL_COUNT_HEADER = "X-Page-Total-Count";
  /**
   * Default Http Header for the total number of elements.
   */
  public static final String DEFAULT_TOTAL_COUNT_HEADER = "X-Total-Count";

  private String pageHeader;

  private String pageSizeHeader;

  private String pageCountHeader;

  private String pageTotalCountHeader;

  private String totalCountHeader;

  /**
   * Construct a new {@link MappingJackson2HttpMessageConverter} using default
   * configuration provided by {@link Jackson2ObjectMapperBuilder}.
   */
  public PageMappingJackson2HttpMessageConverter() {
    this(Jackson2ObjectMapperBuilder.json().build());
    log.debug("PageMappingJackson2HttpMessageConverter() - start");
    log.debug("PageMappingJackson2HttpMessageConverter() - end");
  }

  /**
   * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom
   * {@link ObjectMapper}. You can use {@link Jackson2ObjectMapperBuilder} to
   * build it easily.
   * 
   * @see Jackson2ObjectMapperBuilder#json()
   * 
   * @param objectMapper the ObjectMapper
   */
  public PageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(objectMapper);
    log.debug("PageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) - start");
    log.debug("PageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) - end");
  }

  /**
   * Add default headers to the output message.
   * <p>
   * This implementation delegates to {@link #getDefaultContentType(Object)} if a
   * content type was not provided, set if necessary the default character set,
   * calls {@link #getContentLength}, and sets the corresponding headers.
   * 
   * @param headers     the HttpHeaders
   * @param t           the Object
   * @param contentType the MediaType
   * @throws IOException if a problem occurs
   */
  @Override
  protected void addDefaultHeaders(HttpHeaders headers, Object t, MediaType contentType) throws IOException {
    log.debug("addDefaultHeaders(HttpHeaders headers, Object t, MediaType contentType) - start");
    super.addDefaultHeaders(headers, t, contentType);
    if (t instanceof Page) {
      log.info("Adding pagination headers");
      Page<?> page = (Page<?>) t;
      // Page index
      headers.add(getPageHeader(), String.valueOf(page.getNumber()));
      // Elements per page
      headers.add(getPageSizeHeader(), String.valueOf(page.getSize()));
      // Elements in this page
      headers.add(getPageTotalCountHeader(), String.valueOf(page.getNumberOfElements()));
      // Total number of pages
      headers.add(getPageCountHeader(), String.valueOf(page.getTotalPages()));
      // Total amount of elements
      headers.add(getTotalCountHeader(), String.valueOf(page.getTotalElements()));
    }
    log.debug("addDefaultHeaders(HttpHeaders headers, Object t, MediaType contentType) - end");
  }

  /**
   * Method that writes the actual body. Invoked from {@link #write}.
   * 
   * @param object        the object to write to the output message
   * @param type          the type of object to write (may be {@code null})
   * @param outputMessage the HTTP output message to write to
   * @throws IOException                     in case of I/O errors
   * @throws HttpMessageNotWritableException in case of conversion errors
   */
  @Override
  protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    log.debug("writeInternal(Object object, Type type, HttpOutputMessage outputMessage) - start");
    if (object instanceof Page) {
      // Extract page content
      log.info("Extracting page content");
      Page<?> page = (Page<?>) object;
      object = page.getContent();
    }
    super.writeInternal(object, type, outputMessage);
    log.debug("writeInternal(Object object, Type type, HttpOutputMessage outputMessage) - end");
  }

  /**
   * Get the header name used for page number.
   * 
   * @return String the header name used for page number
   */
  public String getPageHeader() {
    log.debug("getPageHeader() - start");
    if (pageHeader != null) {
      log.info("Customized page header found");
      log.debug("getPageHeader() - end");
      return pageHeader;
    }
    log.debug("getPageHeader() - end");
    return DEFAULT_PAGE_HEADER;
  }

  /**
   * Get the header name used for page size.
   * 
   * @return String the header name used for page size
   */
  public String getPageSizeHeader() {
    log.debug("getPageSizeHeader() - start");
    if (pageSizeHeader != null) {
      log.info("Customized page size header found");
      log.debug("getPageSizeHeader() - end");
      return pageSizeHeader;
    }
    log.debug("getPageSizeHeader() - end");
    return DEFAULT_PAGE_SIZE_HEADER;
  }

  /**
   * Get the header name used for page count.
   * 
   * @return String the header name used for page count
   */
  public String getPageCountHeader() {
    log.debug("getPageCountHeader() - start");
    if (pageCountHeader != null) {
      log.info("Customized page count header found");
      log.debug("getPageCountHeader() - end");
      return pageCountHeader;
    }
    log.debug("getPageCountHeader() - end");
    return DEFAULT_PAGE_COUNT_HEADER;
  }

  /**
   * Get the header name used for total page count.
   * 
   * @return String the header name used for total page count
   */
  public String getPageTotalCountHeader() {
    log.debug("getPageTotalCountHeader() - start");
    if (pageTotalCountHeader != null) {
      log.info("Customized page total count header found");
      log.debug("getPageTotalCountHeader() - end");
      return pageTotalCountHeader;
    }
    log.debug("getPageTotalCountHeader() - end");
    return DEFAULT_PAGE_TOTAL_COUNT_HEADER;
  }

  /**
   * Get the header name used for total count.
   * 
   * @return String the header name used for total count
   */
  public String getTotalCountHeader() {
    log.debug("getTotalCountHeader() - start");
    if (totalCountHeader != null) {
      log.info("Customized total count header found");
      log.debug("getTotalCountHeader() - end");
      return totalCountHeader;
    }
    log.debug("getTotalCountHeader() - end");
    return DEFAULT_TOTAL_COUNT_HEADER;
  }

  /**
   * Set the header name used for page number.
   * 
   * @param pageHeader the header name used for page number
   */
  public void setPageHeader(String pageHeader) {
    log.debug("setPageHeader(String pageHeader) - start");
    this.pageHeader = pageHeader;
    log.debug("setPageHeader(String pageHeader) - end");
  }

  /**
   * Set the header name used for page size.
   * 
   * @param pageSizeHeader the header name used for page size
   */
  public void setPageSizeHeader(String pageSizeHeader) {
    log.debug("setPageSizeHeader(String pageSizeHeader) - start");
    this.pageSizeHeader = pageSizeHeader;
    log.debug("setPageSizeHeader(String pageSizeHeader) - end");
  }

  /**
   * Set the header name used for page count.
   * 
   * @param pageCountHeader the header name used for page count
   */
  public void setPageCountHeader(String pageCountHeader) {
    log.debug("setPageCountHeader(String pageCountHeader) - start");
    this.pageCountHeader = pageCountHeader;
    log.debug("setPageCountHeader(String pageCountHeader) - end");
  }

  /**
   * Set the header name used for total page count.
   * 
   * @param pageTotalCountHeader the header name used for total page count
   */
  public void setPageTotalCountHeader(String pageTotalCountHeader) {
    log.debug("setPageTotalCountHeader(String pageTotalCountHeader) - start");
    this.pageTotalCountHeader = pageTotalCountHeader;
    log.debug("setPageTotalCountHeader(String pageTotalCountHeader) - end");
  }

  /**
   * Set the header name used for total count.
   * 
   * @param totalCountHeader the header name used for total count
   */
  public void setTotalCountHeader(String totalCountHeader) {
    log.debug("setTotalCountHeader(String totalCountHeader) - start");
    this.totalCountHeader = totalCountHeader;
    log.debug("setTotalCountHeader(String totalCountHeader) - end");
  }

}