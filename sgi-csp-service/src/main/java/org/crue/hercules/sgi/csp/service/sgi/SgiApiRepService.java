package org.crue.hercules.sgi.csp.service.sgi;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.csp.exceptions.rep.GetDataReportException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service de llamada a microservicio de reporting
 */
@Component
@Slf4j
public class SgiApiRepService extends SgiApiBaseService {

  private static final String ID = "id";
  private static final String ENTITY = "entity";
  private static final String FIELD = "field";
  private static final String NOT_NULL = "notNull";

  public SgiApiRepService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve un informe Autorizacion en pdf
   *
   * @param idAutorizacion Identificador de la Autorizacion
   * @return Resource informe
   */
  public Resource getInformeAutorizacion(Long idAutorizacion) {
    log.debug("getInformeAutorizacion(Long idAutorizacion)- start");
    Assert.notNull(
        idAutorizacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Autorizacion.class)).build());
    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/csp/autorizacion-proyecto-externo/{id}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idAutorizacion).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeAutorizacion(Long idAutorizacion) - end");
    return informe;
  }
}