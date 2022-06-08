package org.crue.hercules.sgi.prc.service.sgi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.prc.dto.sgp.DireccionTesisDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.DatosAcademicosDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.prc.dto.sgp.SexenioDto;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiSgpService extends SgiApiBaseService {

  public SgiApiSgpService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve datos de una persona a través de una consulta al ESB SGP
   *
   * @param personaRef String
   * @return Optional de {@link PersonaDto}
   */
  public Optional<PersonaDto> findPersonaById(String personaRef) {
    log.debug("findById(personaRef)- start");
    Optional<PersonaDto> persona = Optional.empty();

    if (StringUtils.hasText(personaRef)) {
      try {
        ServiceType serviceType = ServiceType.SGP;
        String relativeUrl = "/personas/{personaRef}";
        HttpMethod httpMethod = HttpMethod.GET;
        String mergedURL = buildUri(serviceType, relativeUrl);

        final PersonaDto response = super.<PersonaDto>callEndpoint(mergedURL, httpMethod,
            new ParameterizedTypeReference<PersonaDto>() {
            }, personaRef).getBody();

        persona = Optional.of(response);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new MicroserviceCallException();
      }
    }

    log.debug("findById(personaRef)- end");
    return persona;
  }

  /**
   * Devuelve datos de vinculación de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return Optional de {@link VinculacionDto}
   * 
   */
  public Optional<VinculacionDto> findVinculacionByPersonaId(String personaRef) {
    log.debug("findVinculacionByPersonaId(personaRef)- start");
    Optional<VinculacionDto> vinculacion = Optional.empty();

    try {

      ServiceType serviceType = ServiceType.SGP;
      HttpMethod httpMethod = HttpMethod.GET;

      StringBuilder relativeUrl = new StringBuilder();
      relativeUrl.append("/vinculaciones/persona/");
      relativeUrl.append(personaRef);
      String mergedURL = buildUri(serviceType, relativeUrl.toString());

      final VinculacionDto response = super.<VinculacionDto>callEndpoint(mergedURL,
          httpMethod, new ParameterizedTypeReference<VinculacionDto>() {
          }, personaRef).getBody();

      vinculacion = Optional.of(response);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findVinculacionByPersonaId(personaRef)- end");

    return vinculacion;
  }

  /**
   * Devuelve datos de contacto de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return Optional de {@link DatosContactoDto}
   */
  public Optional<DatosContactoDto> findDatosContactoByPersonaId(String personaRef) {
    log.debug("findDatosContactoByPersonaId(personaRef)- start");
    Optional<DatosContactoDto> datosContacto = Optional.empty();

    try {

      ServiceType serviceType = ServiceType.SGP;
      HttpMethod httpMethod = HttpMethod.GET;

      StringBuilder relativeUrl = new StringBuilder();
      relativeUrl.append("/datos-contacto/persona/");
      relativeUrl.append(personaRef);
      String mergedURL = buildUri(serviceType, relativeUrl.toString());

      final DatosContactoDto response = super.<DatosContactoDto>callEndpoint(mergedURL,
          httpMethod, new ParameterizedTypeReference<DatosContactoDto>() {
          }, personaRef).getBody();

      datosContacto = Optional.of(response);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findDatosContactoByPersonaId(personaRef)- end");

    return datosContacto;
  }

  /**
   * Devuelve datos académicos de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return Optional de {@link DatosAcademicosDto}
   * 
   */
  public Optional<DatosAcademicosDto> findDatosAcademicosByPersonaId(String personaRef) {
    log.debug("findDatosAcademicosByPersonaId(personaRef)- start");
    Optional<DatosAcademicosDto> datosAcademicos = Optional.empty();

    try {
      ServiceType serviceType = ServiceType.SGP;
      HttpMethod httpMethod = HttpMethod.GET;

      StringBuilder relativeUrl = new StringBuilder();
      relativeUrl.append("/datos-academicos/persona/");
      relativeUrl.append(personaRef);
      String mergedURL = buildUri(serviceType, relativeUrl.toString());

      final DatosAcademicosDto response = super.<DatosAcademicosDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<DatosAcademicosDto>() {
          }, personaRef).getBody();

      datosAcademicos = Optional.of(response);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findDatosAcademicosByPersonaId(personaRef)- end");

    return datosAcademicos;
  }

  /**
   * Devuelve los sexenios de todos las personas de un determinado año a través de
   * una consulta al ESB
   *
   * @param fechaFinBaremacion Fecha fin de baremación
   * @return Lista de {@link SexenioDto}
   * 
   */
  public List<SexenioDto> findSexeniosByFecha(String fechaFinBaremacion) {
    log.debug("findSexeniosByAnio({})- start", fechaFinBaremacion);
    List<SexenioDto> result = new ArrayList<>();

    try {
      ServiceType serviceType = ServiceType.SGP;
      HttpMethod httpMethod = HttpMethod.GET;
      StringBuilder relativeUrl = new StringBuilder();
      relativeUrl.append("/sexenios?fecha=");
      relativeUrl.append(fechaFinBaremacion);
      String mergedURL = buildUri(serviceType, relativeUrl.toString());

      result = super.<List<SexenioDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<SexenioDto>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findSexeniosByAnio({})- end", fechaFinBaremacion);

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());

  }

  /**
   * Devuelve las direcciones de tesis de todos las personas de un determinado año
   * a través de
   * una consulta al ESB
   *
   * @param anio año de baremación
   * @return Lista de {@link DireccionTesisDto}
   * 
   */
  public List<DireccionTesisDto> findTesisByAnio(Integer anio) {
    log.debug("findTesisByAnio({})- start", anio);
    List<DireccionTesisDto> result = new ArrayList<>();

    try {
      ServiceType serviceType = ServiceType.SGP;
      HttpMethod httpMethod = HttpMethod.GET;
      StringBuilder relativeUrl = new StringBuilder();
      relativeUrl.append("/direccion-tesis?anioDefensa=");
      relativeUrl.append(anio.toString());
      String mergedURL = buildUri(serviceType, relativeUrl.toString());

      result = super.<List<DireccionTesisDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<DireccionTesisDto>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findTesisByAnio({})- end", anio);

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());

  }

}
