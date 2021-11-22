package org.crue.hercules.sgi.rep.service.sgp;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.DatosAcademicosDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersonaService {
  private static final String URL_API = "/personas";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public PersonaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
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
      StringBuilder url = new StringBuilder();
      url.append(restApiProperties.getSgpUrl());
      url.append(URL_API);
      url.append("/");
      url.append(personaRef);

      final ResponseEntity<PersonaDto> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), PersonaDto.class);

      persona = response.getBody();
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

      StringBuilder url = new StringBuilder();
      url.append(restApiProperties.getSgpUrl());
      url.append("/datos-contacto/persona/");
      url.append(personaRef);

      final ResponseEntity<DatosContactoDto> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), DatosContactoDto.class);

      datosContacto = response.getBody();

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

      StringBuilder url = new StringBuilder();
      url.append(restApiProperties.getSgpUrl());
      url.append("/datos-academicos/persona/");
      url.append(personaRef);

      final ResponseEntity<DatosAcademicosDto> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), DatosAcademicosDto.class);

      datosAcademicos = response.getBody();

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

      StringBuilder url = new StringBuilder();
      url.append(restApiProperties.getSgpUrl());
      url.append("/vinculaciones/persona/");
      url.append(personaRef);

      final ResponseEntity<VinculacionDto> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), VinculacionDto.class);

      vinculacion = response.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return vinculacion;
  }

}
