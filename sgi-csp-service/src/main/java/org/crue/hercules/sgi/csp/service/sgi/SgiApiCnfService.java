package org.crue.hercules.sgi.csp.service.sgi;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.cnf.ConfigOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiCnfService extends SgiApiBaseService {
  public static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  public static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  public static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  public static final String MESSAGE_KEY_NAME = "name";

  public static final String CLIENT_REGISTRATION_ID = "csp-service";

  private final ObjectMapper mapper;

  public SgiApiCnfService(RestApiProperties restApiProperties, RestTemplate restTemplate, ObjectMapper mapper) {
    super(restApiProperties, restTemplate);
    this.mapper = mapper;
  }

  public String findByName(String name) {
    log.debug("findByName(String name) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                ConfigOutput.class))
            .build());

    ServiceType serviceType = ServiceType.CNF;
    String relativeUrl = "/config/{name}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final ConfigOutput response = super.<ConfigOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<ConfigOutput>() {
        }, name).getBody();

    String returnValue = null;
    if (response != null) {
      returnValue = response.getValue();
    }
    log.debug("findByName(String name) - end");
    return returnValue;
  }

  public List<String> findStringListByName(String name) throws JsonProcessingException {
    log.debug("findStringListByName(String name) - start");
    String value = findByName(name);
    List<String> valueList = null;
    if (value == null) {
      valueList = Collections.emptyList();
    } else {
      valueList = mapper.readValue(value,
          TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
    }

    log.debug("findStringListByName(String name) - end");
    return valueList;
  }

}
