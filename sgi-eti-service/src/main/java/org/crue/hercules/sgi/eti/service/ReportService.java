package org.crue.hercules.sgi.eti.service;

import java.time.Instant;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.eti.exceptions.rep.GetDataReportException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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
public class ReportService {

  private static final String URL_API = "/report/eti";

  private static final String ID = "id";
  private static final String ENTITY = "entity";
  private static final String FIELD = "field";
  private static final String NOT_NULL = "notNull";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public ReportService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Devuelve un informe del formulario M10, M20 o M30 en pdf
   *
   * @param idMemoria    Id de la memoria
   * @param idFormulario id del formulario
   * @return Resource informe
   */
  public Resource getMXX(Long idMemoria, Long idFormulario) {
    log.debug("getMXX(Long idMemoria, Long idFormulario)- start");
    Assert.notNull(idMemoria,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Memoria.class)).build());

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-mxx/" + idMemoria + "/" + idFormulario, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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

    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Evaluacion.class)).build());

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-evaluacion/" + idEvaluacion, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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
    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Evaluacion.class)).build());

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-ficha-evaluador/" + idEvaluacion, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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
    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Evaluacion.class)).build());

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-favorable-memoria/" + idEvaluacion, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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
   * 
   * @return Resource informe
   */
  public Resource getInformeFavorableRatificacion(Long idEvaluacion) {
    log.debug("getInformeFavorableRatificacion(idEvaluacion)- start");
    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Evaluacion.class)).build());

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-favorable-ratificacion/" + idEvaluacion, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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
   * 
   * @return Resource informe
   */
  public Resource getInformeFavorableModificacion(Long idEvaluacion) {
    log.debug("getInformeFavorableModificacion(idEvaluacion)- start");
    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Evaluacion.class)).build());

    Resource informe = null;
    try {
      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-favorable-modificacion/" + idEvaluacion, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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
   * @return Resource informe
   */
  public Resource getInformeActa(Long idActa) {
    log.debug("getInformeActa(idActa)- start");
    Assert.notNull(idActa,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Acta.class)).build());
    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-acta/" + idActa, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
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
   * 
   * @return Resource informe
   */
  public Resource getInformeEvaluacionRetrospectiva(Long idEvaluacion, Instant fecha) {
    log.debug("getInformeEvaluacionRetrospectiva(idEvaluacion, fecha)- start");
    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Evaluacion.class)).build());

    InformeEvaluacionReportInput input = InformeEvaluacionReportInput.builder().idEvaluacion(idEvaluacion).fecha(fecha)
        .build();

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/informe-evaluacion-retrospectiva", HttpMethod.POST,
          new HttpEntityBuilder<>(input).withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeEvaluacionRetrospectiva(idEvaluacion, fecha)- end");
    return informe;
  }
}