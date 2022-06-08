package org.crue.hercules.sgi.prc.service.sgi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.dto.csp.GrupoEquipoDto;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoDto;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoEquipoDto;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoProyectoSgeDto;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiCspService extends SgiApiBaseService {

  public SgiApiCspService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene el grupo de investigación por su id
   *
   * @param grupoRef grupoRef
   * @return GrupoDto
   */
  public Optional<GrupoDto> findGrupoById(Long grupoRef) {
    log.debug("findGrupoById({})- start", grupoRef);

    Optional<GrupoDto> grupoDto = Optional.empty();
    if (Objects.nonNull(grupoRef)) {
      try {

        ServiceType serviceType = ServiceType.CSP;
        String relativeUrl = "/grupos/{grupoRef}";
        HttpMethod httpMethod = HttpMethod.GET;
        String mergedURL = buildUri(serviceType, relativeUrl);

        final GrupoDto response = super.<GrupoDto>callEndpoint(mergedURL, httpMethod,
            new ParameterizedTypeReference<GrupoDto>() {
            }, grupoRef).getBody();

        grupoDto = Optional.of(response);

      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new MicroserviceCallException();
      }
    }

    log.debug("findGrupoById({})- end", grupoRef);
    return grupoDto;
  }

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef grupoRef
   * @param anio     año de baremación
   * @return true/false
   */
  public Boolean isGrupoBaremable(Long grupoRef, Integer anio) {
    log.debug("isGrupoBaremable(grupoRef, anio)- start");
    Boolean isValid = Boolean.FALSE;

    if (Objects.nonNull(grupoRef) && Objects.nonNull(anio)) {
      try {

        ServiceType serviceType = ServiceType.CSP;
        String relativeUrl = "/grupos/grupo-baremable/{grupoRef}/{anio}";
        HttpMethod httpMethod = HttpMethod.GET;
        String mergedURL = buildUri(serviceType, relativeUrl);

        final Boolean response = super.<Boolean>callEndpoint(mergedURL, httpMethod,
            new ParameterizedTypeReference<Boolean>() {
            }, grupoRef, anio).getBody();

        isValid = response;
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new MicroserviceCallException();
      }
    }
    log.debug("isGrupoBaremable(grupoRef, anio)- end");
    return isValid;
  }

  /**
   * Devuelve una lista de {@link GrupoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   * 
   * @param anio año de baremación
   * @return lista de {@link GrupoDto}
   */
  public List<GrupoDto> findAllGruposByAnio(Integer anio) {
    List<GrupoDto> result = new ArrayList<>();
    log.debug("findAllByAnio(anio)- start");

    try {
      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/grupos/baremables/{anio}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<GrupoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<GrupoDto>>() {
          }, anio).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findAllByAnio(anio)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Comprueba si personaRef pertenece a un grupo de investigación con un rol con
   * el flag de baremable a true a fecha 31 de diciembre del año que se esta
   * baremando y el grupo al que pertenecen los autores (tabla Grupo) este activo
   * y el campo "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param personaRef personaRef
   * @param anio       año de baremación
   * @return true/false
   */
  public Boolean isPersonaBaremable(String personaRef, Integer anio) {
    log.debug("isPersonaBaremable(personaRef, anio)- start");
    Boolean isValid = Boolean.FALSE;

    if (StringUtils.hasText(personaRef) && Objects.nonNull(anio)) {
      try {

        ServiceType serviceType = ServiceType.CSP;
        String relativeUrl = "/gruposequipos/persona-baremable/{personaRef}/{anio}";
        HttpMethod httpMethod = HttpMethod.HEAD;
        String mergedURL = buildUri(serviceType, relativeUrl);

        final ResponseEntity<Void> response = super.<Void>callEndpoint(mergedURL,
            httpMethod, new ParameterizedTypeReference<Void>() {
            }, personaRef, anio);

        isValid = response.getStatusCode().equals(HttpStatus.OK);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new MicroserviceCallException();
      }
    }
    log.debug("isPersonaBaremable(personaRef, anio)- end");

    return isValid;
  }

  /**
   * Devuelve una lista de {@link GrupoEquipoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param grupoRef grupoRef
   * @param anio     año de baremación
   * @return lista de {@link GrupoEquipoDto}
   */
  public List<GrupoEquipoDto> findAllGruposEquipoByGrupoIdAndAnio(Long grupoRef, Integer anio) {
    List<GrupoEquipoDto> result = new ArrayList<>();
    log.debug("findByGrupoIdAndAnio(grupoRef, anio)- start");

    try {

      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/gruposequipos/baremables/{grupoRef}/{anio}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<GrupoEquipoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<GrupoEquipoDto>>() {
          }, grupoRef, anio)
          .getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findByGrupoIdAndAnio(grupoRef, anio)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Lista de ids {@link GrupoEquipoDto} cuyo personaRef está dentro de la fecha
   * de
   * baremación
   *
   * @param personaRef personaRef
   * @param anio       anio
   * @return lista de ids {@link GrupoEquipoDto}
   */
  public List<Long> findGrupoEquipoByPersonaRefAndFechaBaremacion(String personaRef, Integer anio) {
    List<Long> result = new ArrayList<>();
    log.debug("findGrupoEquipoByPersonaRefAndFechaBaremacion({},{})- start", personaRef, anio);

    try {

      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/gruposequipos/{grupoRef}/{anio}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<Long>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<Long>>() {
          },
          personaRef, anio)
          .getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findGrupoEquipoByPersonaRefAndFechaBaremacion({},{})- end", personaRef, anio);

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   * 
   * @param anioInicio año inicio de baremación
   * @param anioFin    año fin de baremación
   * @return lista de {@link ProyectoDto}
   */
  public List<ProyectoDto> findProyectosProduccionCientifica(Integer anioInicio, Integer anioFin) {
    List<ProyectoDto> result = new ArrayList<>();
    log.debug("findProyectosProduccionCientifica(anioInicio, anioFin)- start");

    try {
      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/proyectos/produccioncientifica/{anioInicio}/{anioFin}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<ProyectoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<ProyectoDto>>() {
          }, anioInicio, anioFin).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findProyectosProduccionCientifica(anioInicio, anioFin)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Obtiene la suma de importe concedido de cada AnualidadGasto
   * asociados a un {@link ProyectoDto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link ProyectoDto}
   * @return suma de puntos del campo importeConcedido
   */
  public BigDecimal getTotalImporteConcedidoAnualidadGasto(Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGasto(proyectoId)- start");

    String relativeUrl = "/proyectos/produccioncientifica/totalimporteconcedido/{proyectoId}";
    BigDecimal result = callProyectosByProyectoIdAndReturnBigDecimal(proyectoId, relativeUrl);
    log.debug("getTotalImporteConcedidoAnualidadGasto(proyectoId)- end");

    return result;
  }

  private BigDecimal callProyectosByProyectoIdAndReturnBigDecimal(Long proyectoId, String relativeUrl) {
    BigDecimal result = null;

    try {
      ServiceType serviceType = ServiceType.CSP;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<BigDecimal>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<BigDecimal>() {
          }, proyectoId).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    return result;
  }

  /**
   * Obtiene la suma de importe concedido de cada AnualidadGasto de costes
   * indirectos asociados a un {@link ProyectoDto} cuyo id coincide con el
   * indicado.
   * 
   * @param proyectoId el identificador del {@link ProyectoDto}
   * @return suma de puntos del campo importeConcedido
   */
  public BigDecimal getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyectoId)- start");

    String relativeUrl = "/proyectos/produccioncientifica/totalimporteconcedidocostesindirectos/{proyectoId}";

    BigDecimal result = callProyectosByProyectoIdAndReturnBigDecimal(proyectoId, relativeUrl);

    log.debug("getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyectoId)- end");

    return result;
  }

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   *
   * @param proyectoId id de {@link ProyectoDto}
   * @return lista de {@link ProyectoDto}
   */
  public List<ProyectoEquipoDto> findProyectoEquipoByProyectoId(Long proyectoId) {
    List<ProyectoEquipoDto> result = new ArrayList<>();
    log.debug("findProyectoEquipoByProyectoId(proyectoId)- start");

    try {
      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/proyectos/produccioncientifica/equipo/{proyectoId}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<ProyectoEquipoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<ProyectoEquipoDto>>() {
          }, proyectoId).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findProyectoEquipoByProyectoId(proyectoId)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Devuelve una lista de {@link ProyectoProyectoSgeDto} de un determinado
   * {@link ProyectoDto}
   *
   * @param proyectoId id de {@link ProyectoProyectoSgeDto}
   * @return lista de {@link ProyectoProyectoSgeDto}
   */
  public List<ProyectoProyectoSgeDto> findProyectosSgeByProyectoId(Long proyectoId) {
    List<ProyectoProyectoSgeDto> result = new ArrayList<>();
    log.debug("findProyectosSGEByProyectoId(proyectoId)- start");

    try {
      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/proyectos/{proyectoId}/proyectossge";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<ProyectoProyectoSgeDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<ProyectoProyectoSgeDto>>() {
          }, proyectoId).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findProyectosSGEByProyectoId(proyectoId)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Devuelve la lista de investigadores principales de un determinado grupo
   *
   * @param grupoRef grupoRef
   * @return lista de investigadores principales
   */
  public List<String> findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(Long grupoRef) {
    List<String> result = new ArrayList<>();
    log.debug("findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion({}) start", grupoRef);

    try {
      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/grupos/{grupoRef}/investigadoresprincipalesmaxparticipacion";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<String>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<String>>() {
          }, grupoRef).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion({}) end", grupoRef);

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Devuelve una lista de {@link GrupoDto} donde la personaRef es investigador
   * principal o persona autorizada para la fecha actual
   * 
   * 
   * @param personaRef identificador externo de la persona.
   * @return lista de {@link GrupoDto}
   */
  public List<GrupoDto> findAllGruposByPersonaRef(String personaRef) {
    List<GrupoDto> result = new ArrayList<>();
    log.debug("findAllGruposByPersonaRef(String personaRef)- start");

    try {
      ServiceType serviceType = ServiceType.CSP;
      String relativeUrl = "/grupos?q=responsable==" + personaRef + ",personaAutorizada==" + personaRef;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<GrupoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<GrupoDto>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findAllGruposByPersonaRef(String personaRef)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

}
