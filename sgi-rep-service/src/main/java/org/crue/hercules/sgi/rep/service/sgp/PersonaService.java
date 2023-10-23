package org.crue.hercules.sgi.rep.service.sgp;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.DatosAcademicosDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiBaseService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonaService extends SgiApiBaseService {
  private static final String PATH_DELIMITER = "/";
  private static final String URL_API = PATH_DELIMITER + "personas";

  private static final String PATH_ID = URL_API + PATH_DELIMITER + "{id}";

  private static final String PATH_DATOS_ACADEMICOS = PATH_DELIMITER + "datos-academicos/persona/{id}";
  private static final String PATH_DATOS_CONTACTO = PATH_DELIMITER + "datos-contacto/persona/{id}";
  private static final String PATH_VINCULACIONES = PATH_DELIMITER + "vinculaciones/persona/{id}";

  public PersonaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve datos de una persona a través de una consulta al ESB
   *
   * @param id Identificador de la persona
   * @return una persona
   */
  public PersonaDto findById(String id) {
    log.debug("findById({}) - start", id);
    PersonaDto persona = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = PATH_ID;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      persona = super.<PersonaDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<PersonaDto>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("findById({}) - end", id);
    return persona;
  }

  /**
   * Devuelve datos de contacto de una persona a través de una consulta al ESB
   *
   * @param id Identificador de la persona
   * @return DatosContactoDto
   */
  public DatosContactoDto getDatosContacto(String id) {
    log.debug("findByPersonaId({}) - start", id);
    DatosContactoDto datosContacto = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = PATH_DATOS_CONTACTO;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      datosContacto = super.<DatosContactoDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<DatosContactoDto>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("findByPersonaId({}) - end", id);
    return datosContacto;
  }

  /**
   * Devuelve datos académicos de una persona a través de una consulta al ESB
   *
   * @param id Identificador de la persona
   * @return DatosAcademicosDto
   */
  public DatosAcademicosDto getDatosAcademicos(String id) {
    log.debug("getDatosAcademicos({}) - start", id);
    DatosAcademicosDto datosAcademicos = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = PATH_DATOS_ACADEMICOS;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      datosAcademicos = super.<DatosAcademicosDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<DatosAcademicosDto>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getDatosAcademicos({}) - end", id);
    return datosAcademicos;
  }

  /**
   * Devuelve datos de vinculación de una persona a través de una consulta al ESB
   *
   * @param id Identificador de la persona
   * @return VinculacionDto
   */
  public VinculacionDto getVinculacion(String id) {
    log.debug("getVinculacion({}) - start", id);
    VinculacionDto vinculacion = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = PATH_VINCULACIONES;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      vinculacion = super.<VinculacionDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<VinculacionDto>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getVinculacion({}) - end", id);
    return vinculacion;
  }

}
