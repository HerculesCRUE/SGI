package org.crue.hercules.sgi.rep.service.sgi;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.DatosAcademicosDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiSgpService extends SgiApiBaseService {

  public SgiApiSgpService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve datos de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return PersonaDto
   */
  public PersonaDto findById(String personaRef) {
    log.debug("findById(personaRef)- start");
    PersonaDto persona = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = "/personas/{personaRef}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      persona = super.<PersonaDto>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<PersonaDto>() {
          }, personaRef).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return persona;
  }

  /**
   * Devuelve datos de contacto de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return DatosContactoDto
   */
  public DatosContactoDto findDatosContactoByPersonaId(String personaRef) {
    log.debug("findDatosContactoByPersonaId(personaRef)- start");
    DatosContactoDto datosContacto = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = "/datos-contacto/persona/{personaRef}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      datosContacto = super.<DatosContactoDto>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<DatosContactoDto>() {
          }, personaRef).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return datosContacto;
  }

  /**
   * Devuelve datos académicos de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return DatosAcademicosDto
   */
  public DatosAcademicosDto findDatosAcademicosByPersonaId(String personaRef) {
    log.debug("findDatosAcademicosByPersonaId(personaRef)- start");
    DatosAcademicosDto datosAcademicos = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = "/datos-academicos/persona/{personaRef}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      datosAcademicos = super.<DatosAcademicosDto>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<DatosAcademicosDto>() {
          }, personaRef).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return datosAcademicos;
  }

  /**
   * Devuelve datos de vinculación de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return VinculacionDto
   */
  public VinculacionDto findVinculacionByPersonaId(String personaRef) {
    log.debug("findVinculacionByPersonaId(personaRef)- start");
    VinculacionDto vinculacion = null;
    try {
      ServiceType serviceType = ServiceType.SGP;
      String relativeUrl = "/vinculaciones/persona/{personaRef}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      vinculacion = super.<VinculacionDto>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<VinculacionDto>() {
          }, personaRef).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return vinculacion;
  }

}
