package org.crue.hercules.sgi.csp.service.sgi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.sgemp.EmpresaOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.csp.exceptions.SgiApiSgempFindEmpresaIdsByPaisIdException;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiSgempService extends SgiApiBaseService {

  public SgiApiSgempService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene los datos de una entidad, de SGEMP, a través de su identificador
   * 
   * @param id Identificador de la entidad
   * @return {@link EmpresaOutput}
   */
  public EmpresaOutput findById(String id) {
    log.debug("findById({}) - start", id);

    AssertHelper.fieldNotNull(id, EmpresaOutput.class, AssertHelper.MESSAGE_KEY_ID);

    ServiceType serviceType = ServiceType.SGEMP;
    String relativeUrl = "/empresas/{id}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final EmpresaOutput response = super.<EmpresaOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<EmpresaOutput>() {
        }, id).getBody();

    log.debug("findById({}) - end", id);
    return response;
  }

  /**
   * Obtiene los ids de las empresas pertenecientes al país
   * 
   * @param paisId Identificador del país
   * @return Lista de ids de empresas
   */
  public List<String> findAllEmpresaIdsByPaisId(String paisId) {
    log.debug("findAllEmpresaIdsByPaisId({}) - start", paisId);

    AssertHelper.fieldNotNull(paisId, EmpresaOutput.class, "paisId");

    ServiceType serviceType = ServiceType.SGEMP;
    String relativeUrl = "/empresas?q=paisId=={paisId}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    List<String> empresaIds = Collections.emptyList();
    try {
      empresaIds = Optional.ofNullable(
          super.<List<EmpresaOutput>>callEndpoint(mergedURL, httpMethod,
              new ParameterizedTypeReference<List<EmpresaOutput>>() {
              }, paisId)
              .getBody())
          .orElse(Collections.emptyList())
          .stream()
          .map(EmpresaOutput::getId)
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("findAllEmpresaIdsByPaisId({}) -", paisId, e);
      throw new SgiApiSgempFindEmpresaIdsByPaisIdException();
    }

    log.debug("findAllEmpresaIdsByPaisId({}) - end", paisId);
    return empresaIds;
  }

  /**
   * Obtienes los datos de varias entidades, de SGP, a través de sus
   * identificadoes
   * 
   * @param ids Listado de identificadores de entidad
   * @return Listado de {@link EmpresaOutput}
   */
  public List<EmpresaOutput> findAllByIdIn(List<String> ids) {
    log.debug("findAllByIdIn({}) - start", ids);

    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    String in = ids.stream()
        .filter(StringUtils::isNotBlank)
        .map(id -> StringUtils.wrap(id, "\""))
        .collect(Collectors.joining(","));

    ServiceType serviceType = ServiceType.SGEMP;
    String relativeUrl = "/empresas/?q=id=in=({in})";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final List<EmpresaOutput> response = super.<List<EmpresaOutput>>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<List<EmpresaOutput>>() {
        }, in).getBody();

    log.debug("findAllByIdIn({}) - end", ids);
    return response;
  }

}
