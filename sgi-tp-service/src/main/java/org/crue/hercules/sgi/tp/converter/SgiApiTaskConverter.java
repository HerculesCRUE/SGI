package org.crue.hercules.sgi.tp.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.tp.dto.SgiApiCronTaskInput;
import org.crue.hercules.sgi.tp.dto.SgiApiCronTaskOutput;
import org.crue.hercules.sgi.tp.dto.SgiApiInstantTaskInput;
import org.crue.hercules.sgi.tp.dto.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.tp.enums.ServiceType;
import org.crue.hercules.sgi.tp.model.BeanMethodCronTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;

public class SgiApiTaskConverter {
  public static final String BEAN_NAME_SGI_API_CALLER_TASK = "sgiApiCallerTask";

  private SgiApiTaskConverter() {
    throw new IllegalStateException("Utility class");
  }

  public static BeanMethodCronTask convert(Long id, SgiApiCronTaskInput task) {
    BeanMethodCronTask returnValue = convert(task);
    returnValue.setId(id);
    return returnValue;
  }

  public static BeanMethodInstantTask convert(Long id, SgiApiInstantTaskInput task) {
    BeanMethodInstantTask returnValue = convert(task);
    returnValue.setId(id);
    return returnValue;
  }

  public static BeanMethodCronTask convert(SgiApiCronTaskInput cronTask) {
    List<String> params = new ArrayList<>();
    params.add(cronTask.getServiceType().name());
    params.add(cronTask.getRelativeUrl());
    params.add(cronTask.getHttpMethod().name());
    return BeanMethodCronTask.builder().bean(BEAN_NAME_SGI_API_CALLER_TASK).method("call").params(params)
        .description(cronTask.getDescription()).cronExpression(cronTask.getCronExpression()).disabled(Boolean.FALSE)
        .build();
  }

  public static BeanMethodInstantTask convert(SgiApiInstantTaskInput instantTask) {
    List<String> params = new ArrayList<>();
    params.add(instantTask.getServiceType().name());
    params.add(instantTask.getRelativeUrl());
    params.add(instantTask.getHttpMethod().name());
    return BeanMethodInstantTask.builder().bean(BEAN_NAME_SGI_API_CALLER_TASK).method("call").params(params)
        .description(instantTask.getDescription()).instant(instantTask.getInstant()).disabled(Boolean.FALSE).build();
  }

  public static Object convert(BeanMethodTask task) {
    if (task instanceof BeanMethodCronTask) {
      return convert((BeanMethodCronTask) task);
    }
    return convert((BeanMethodInstantTask) task);
  }

  public static SgiApiCronTaskOutput convert(BeanMethodCronTask cronTask) {
    List<String> params = cronTask.getParams();
    // There are three parameters (1-task type, 2-relative url, 3-http method)
    String serviceType = params.isEmpty() ? "" : params.get(0);
    String relativeUrl = params.size() > 1 ? params.get(1) : "/";
    String httpMehtod = params.size() > 2 ? params.get(2) : "GET";

    return SgiApiCronTaskOutput.builder().id(cronTask.getId()).disabled(cronTask.getDisabled())
        .description(cronTask.getDescription()).serviceType(ServiceType.valueOf(serviceType)).relativeUrl(relativeUrl)
        .httpMethod(HttpMethod.valueOf(httpMehtod)).cronExpression(cronTask.getCronExpression()).build();
  }

  public static SgiApiInstantTaskOutput convert(BeanMethodInstantTask instantTask) {
    List<String> params = instantTask.getParams();
    // There are three parameters (1-task type, 2-relative url, 3-http method)
    String serviceType = params.isEmpty() ? "" : params.get(0);
    String relativeUrl = params.size() > 1 ? params.get(1) : "/";
    String httpMehtod = params.size() > 2 ? params.get(2) : "GET";

    return SgiApiInstantTaskOutput.builder().id(instantTask.getId()).disabled(instantTask.getDisabled())
        .description(instantTask.getDescription()).serviceType(ServiceType.valueOf(serviceType))
        .relativeUrl(relativeUrl).httpMethod(HttpMethod.valueOf(httpMehtod)).instant(instantTask.getInstant()).build();
  }

  public static Page<SgiApiCronTaskOutput> convertCron(Page<BeanMethodCronTask> page) {
    List<SgiApiCronTaskOutput> content = page.getContent().stream().map(SgiApiTaskConverter::convert)
        .collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  public static Page<SgiApiInstantTaskOutput> convertInstant(Page<BeanMethodInstantTask> page) {
    List<SgiApiInstantTaskOutput> content = page.getContent().stream().map(SgiApiTaskConverter::convert)
        .collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  public static Page<Object> convert(Page<BeanMethodTask> page) {
    List<Object> content = page.getContent().stream().map(SgiApiTaskConverter::convert).collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
