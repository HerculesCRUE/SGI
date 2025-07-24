package org.crue.hercules.sgi.eti.service.sgi;

import java.time.Instant;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.crue.hercules.sgi.eti.exceptions.rep.GetDataReportException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service de llamada a microservicio de reporting
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SgiApiRepService extends SgiApiBaseService {

  private static final String ID = "id";

  public SgiApiRepService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve un informe del formulario M10, M20 o M30 en pdf
   *
   * @param idMemoria    Id de la memoria
   * @param idFormulario id del formulario
   * @param lang         El {@link Language} en el que obtener el informe.
   * @return Resource informe
   */
  public Resource getMXX(Long idMemoria, Long idFormulario, Language lang) {
    log.debug("getMXX(Long idMemoria, Long idFormulario)- start");
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-mxx/{idMemoria}/{idFormulario}?l={lang}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idMemoria, idFormulario, lang.getCode()).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getMXX(Long idMemoria, Long idFormulario)- end");
    return informe;
  }

  /**
   * Devuelve un informe de evaluación en pdf
   *
   * @param idEvaluacion Id de la evaluación
   * @return Resource informe
   */
  public Resource getInformeEvaluacion(Long idEvaluacion) {
    log.debug("getInformeEvaluacion(idEvaluacion)- start");

    // TODO incluir validaciones de llamada al informe
    // https://confluence.treelogic.com/pages/viewpage.action?pageId=98537005

    AssertHelper.idNotNull(idEvaluacion, Evaluacion.class);

    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-evaluacion/{idEvaluacion}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idEvaluacion).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeEvaluacion(idEvaluacion)- end");
    return informe;
  }

  /**
   * Devuelve un informe de evaluador en pdf
   *
   * @param idEvaluacion Id de la evaluación
   * @return Resource informe
   */
  public Resource getInformeEvaluador(Long idEvaluacion) {
    log.debug("getInformeEvaluador(idEvaluacion)- start");
    AssertHelper.idNotNull(idEvaluacion, Evaluacion.class);

    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-ficha-evaluador/{idEvaluacion}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idEvaluacion).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeEvaluador(idEvaluacion)- end");
    return informe;
  }

  /**
   * Devuelve un informe favorable memoria en pdf
   *
   * @param idEvaluacion Id de la evaluación
   * @return Resource informe
   */
  public Resource getInformeFavorableMemoria(Long idEvaluacion) {
    log.debug("getInformeFavorableMemoria(idEvaluacion)- start");
    AssertHelper.idNotNull(idEvaluacion, Evaluacion.class);
    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-favorable-memoria/{idEvaluacion}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idEvaluacion).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeFavorableMemoria(idEvaluacion)- end");
    return informe;
  }

  /**
   * Devuelve un informe favorable ratificación en pdf
   *
   * @param idEvaluacion Id de la evaluación
   * @return Resource informe
   */
  public Resource getInformeFavorableRatificacion(Long idEvaluacion) {
    log.debug("getInformeFavorableRatificacion(idEvaluacion)- start");
    AssertHelper.idNotNull(idEvaluacion, Evaluacion.class);

    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-favorable-ratificacion/{idEvaluacion}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idEvaluacion).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeFavorableRatificacion(idEvaluacion)- end");
    return informe;
  }

  /**
   * Devuelve un informe favorable modificación en pdf
   *
   * @param idEvaluacion Id de la evaluación
   * @return Resource informe
   */
  public Resource getInformeFavorableModificacion(Long idEvaluacion) {
    log.debug("getInformeFavorableModificacion(idEvaluacion)- start");
    AssertHelper.idNotNull(idEvaluacion, Evaluacion.class);

    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-favorable-modificacion/{idEvaluacion}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idEvaluacion).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeFavorableModificacion(idEvaluacion)- end");
    return informe;
  }

  /**
   * Devuelve un informe acta en pdf
   *
   * @param idActa Id del acta
   * @param lang   El {@link Language} en el que obtener el informe.
   * @return Resource informe
   */
  public Resource getInformeActa(Long idActa, Language lang) {
    log.debug("getInformeActa(idActa)- start");
    AssertHelper.idNotNull(idActa, Acta.class);
    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-acta/{idActa}?l={lang}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<Resource>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<Resource>() {
          }, idActa, lang.getCode()).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeActa(idActa)- end");
    return informe;
  }

  /**
   * Devuelve un informe evaluación retrospectiva en pdf
   *
   * @param idEvaluacion Id de la evaluación
   * @param fecha        Fecha del informe
   * @return Resource informe
   */
  public Resource getInformeEvaluacionRetrospectiva(Long idEvaluacion, Instant fecha) {
    log.debug("getInformeEvaluacionRetrospectiva(idEvaluacion, fecha)- start");
    AssertHelper.idNotNull(idEvaluacion, Evaluacion.class);

    InformeEvaluacionReportInput input = InformeEvaluacionReportInput.builder().idEvaluacion(idEvaluacion).fecha(fecha)
        .build();

    Resource informe = null;
    try {
      ServiceType serviceType = ServiceType.REP;
      String relativeUrl = "/report/eti/informe-evaluacion-retrospectiva";
      HttpMethod httpMethod = HttpMethod.POST;
      String mergedURL = buildUri(serviceType, relativeUrl);

      informe = super.<InformeEvaluacionReportInput, Resource>callEndpointWithCurrentUserAuthorization(mergedURL,
          httpMethod, input, new ParameterizedTypeReference<Resource>() {
          }).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeEvaluacionRetrospectiva(idEvaluacion, fecha)- end");
    return informe;
  }
}