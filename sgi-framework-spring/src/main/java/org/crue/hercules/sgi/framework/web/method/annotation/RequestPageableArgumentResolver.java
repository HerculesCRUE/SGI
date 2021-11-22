package org.crue.hercules.sgi.framework.web.method.annotation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

import lombok.extern.slf4j.Slf4j;

/**
 * Resolves method parameters into argument values in the context of a given
 * request.
 *
 * @see HandlerMethodReturnValueHandler
 */
@Slf4j
public class RequestPageableArgumentResolver implements HandlerMethodArgumentResolver, Serializable {

  /**
   * The {@link SortCriteriaConverter} to convert the sorting information
   */
  private SortCriteriaConverter converter;

  /**
   * Cretates a new {@link RequestPageableArgumentResolver} that uses the provided
   * {@link SortCriteriaConverter} to convert the sorting information provided in
   * the specified sort request parameter.
   * 
   * @param converter the {@link SortCriteriaConverter}
   */
  public RequestPageableArgumentResolver(SortCriteriaConverter converter) {
    log.debug("RequestPageableArgumentResolver(SortCriteriaConverter converter) - start");
    this.converter = converter;
    log.debug("RequestPageableArgumentResolver(SortCriteriaConverter converter) - end");
  }

  /**
   * Whether the given {@linkplain MethodParameter method parameter} is supported
   * by this resolver.
   * 
   * @param parameter the method parameter to check
   * @return {@code true} if this resolver supports the supplied parameter;
   *         {@code false} otherwise
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    log.debug("supportsParameter(MethodParameter parameter) - start");
    boolean returnValue = (parameter.hasParameterAnnotation(RequestPageable.class)
        && Pageable.class.isAssignableFrom(parameter.getNestedParameterType()));
    log.debug("supportsParameter(MethodParameter parameter) - end");
    return returnValue;
  }

  /**
   * Resolves a method parameter into an argument value from a given request. A
   * {@link ModelAndViewContainer} provides access to the model for the request. A
   * {@link WebDataBinderFactory} provides a way to create a {@link WebDataBinder}
   * instance when needed for data binding and type conversion purposes.
   * 
   * @param parameter     the method parameter to resolve. This parameter must
   *                      have previously been passed to
   *                      {@link #supportsParameter} which must have returned
   *                      {@code true}.
   * @param mavContainer  the ModelAndViewContainer for the current request
   * @param webRequest    the current request
   * @param binderFactory a factory for creating {@link WebDataBinder} instances
   * @return the resolved argument value, or {@code null} if not resolvable
   * @throws Exception in case of errors with the preparation of argument values
   */
  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    log.debug(
        "resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) - start");
    MethodParameter nestedParameter = parameter.nestedIfOptional();
    RequestPageable requestPageable = parameter.getParameterAnnotation(RequestPageable.class);
    String xPage = null;
    String xPageSize = null;
    String sortParamName = null;
    if (requestPageable != null) {
      xPage = webRequest.getHeader(requestPageable.pageHeader());
      xPageSize = webRequest.getHeader(requestPageable.pageSizeHeader());
      sortParamName = requestPageable.sort();
    }
    Sort sort = null;

    if (sortParamName != null && !"".equals(sortParamName)) {
      Object arg = resolveName(sortParamName, nestedParameter, webRequest);
      if (arg != null) {
        List<SortCriteria> sortCriterias = converter.convert(arg.toString());
        List<Sort> sortList = generateSortList(sortCriterias);
        sort = andSort(sortList);
      }
    }

    if (xPageSize == null) {
      Object returnValue = new UnpagedPageable(sort != null ? sort : Sort.unsorted());
      log.debug(
          "resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) - end");
      return returnValue;
    }
    if (xPage == null) {
      xPage = "0";
    }
    // Use provided page size and short info
    Object returnValue = PageRequest.of(Integer.parseInt(xPage), Integer.parseInt(xPageSize),
        sort != null ? sort : Sort.unsorted());
    log.debug(
        "resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) - start");
    return returnValue;
  }

  /**
   * @param name      the name
   * @param parameter the MethodParameter
   * @param request   the MethodParameter
   * @return Object
   * @throws Exception if a problem occurs
   */
  protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
    log.debug("resolveName(String name, MethodParameter parameter, NativeWebRequest request) - start");
    if (servletRequest != null) {
      Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
      if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
        log.debug("resolveName(String name, MethodParameter parameter, NativeWebRequest request) - end");
        return mpArg;
      }
    }

    Object arg = null;
    MultipartRequest multipartRequest = request.getNativeRequest(MultipartRequest.class);
    if (multipartRequest != null) {
      List<MultipartFile> files = multipartRequest.getFiles(name);
      if (!files.isEmpty()) {
        arg = (files.size() == 1 ? files.get(0) : files);
      }
    }
    if (arg == null) {
      String[] paramValues = request.getParameterValues(name);
      if (paramValues != null) {
        arg = (paramValues.length == 1 ? paramValues[0] : paramValues);
      }
    }
    log.debug("resolveName(String name, MethodParameter parameter, NativeWebRequest request) - end");
    return arg;
  }

  /**
   * @param criteria the list of {@link SortCriteria}
   * @return List<Sort>
   */
  private List<Sort> generateSortList(List<SortCriteria> criteria) {
    log.debug("generateSortList(List<SortCriteria> criteria) - start");
    List<Sort> returnValue = criteria.stream().map(criterion -> {
      switch (criterion.getOperation()) {
      case ASC:
        return Sort.by(Order.asc(criterion.getKey()));
      case DESC:
        return Sort.by(Order.desc(criterion.getKey()));
      default:
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toList());
    log.debug("generateSortList(List<SortCriteria> criteria) - end");
    return returnValue;
  }

  /**
   * @param criteria the list of {@link Sort}
   * @return Sort
   */
  private Sort andSort(List<Sort> criteria) {
    log.debug("andSort(List<Sort> criteria) - start");
    Iterator<Sort> itr = criteria.iterator();
    if (itr.hasNext()) {
      Sort sort = (itr.next());
      while (itr.hasNext()) {
        sort = sort.and(itr.next());
      }
      log.debug("andSort(List<Sort> criteria) - end");
      return sort;
    }
    log.debug("andSort(List<Sort> criteria) - end");
    return null;
  }

  /**
   * Custom {@link Pageable} for unpaged but sorted page info.
   */
  public static class UnpagedPageable implements Pageable, Serializable {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UnpagedPageable.class);

    /**
     * The sort information
     */
    private Sort sort;

    /**
     * Creates a new {@link UnpagedPageable} with the provided sort information.
     * 
     * @param sort the sorting
     */
    public UnpagedPageable(Sort sort) {
      log.debug("UnpagedPageable(Sort sort) - start");
      this.sort = sort;
      log.debug("UnpagedPageable(Sort sort) - end");
    }

    @Override
    public boolean isPaged() {
      log.debug("isPaged() - start");
      log.debug("isPaged() - end");
      return false;
    }

    @Override
    public Pageable previousOrFirst() {
      log.debug("previousOrFirst() - start");
      log.debug("previousOrFirst() - end");
      return this;
    }

    @Override
    public Pageable next() {
      log.debug("next() - start");
      log.debug("next() - end");
      return this;
    }

    @Override
    public boolean hasPrevious() {
      log.debug("hasPrevious() - start");
      log.debug("hasPrevious() - end");
      return false;
    }

    @Override
    public Sort getSort() {
      log.debug("getSort() - start");
      log.debug("getSort() - end");
      return sort;
    }

    @Override
    @JsonIgnore
    public int getPageSize() {
      log.debug("getPageSize() - start");
      throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    public int getPageNumber() {
      log.debug("getPageNumber() - start");
      throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    public long getOffset() {
      log.debug("getOffset() - start");
      throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
      log.debug("first() - start");
      log.debug("first() - end");
      return this;
    }

  }

}