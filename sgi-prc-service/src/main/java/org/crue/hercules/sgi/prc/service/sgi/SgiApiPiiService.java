package org.crue.hercules.sgi.prc.service.sgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.pi.InvencionDto;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiPiiService extends SgiApiBaseService {

  public SgiApiPiiService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve una lista de {@link InvencionDto} que se incorporarán a la
   * baremación de producción científica
   * 
   * @param anioInicio    año inicio de baremación
   * @param anioFin       año fin de baremación
   * @param universidadId Id de la universidad
   * @return lista de {@link InvencionDto}
   */
  public List<InvencionDto> findInvencionesProduccionCientifica(Integer anioInicio, Integer anioFin,
      String universidadId) {
    List<InvencionDto> result = new ArrayList<>();
    log.debug("findInvencionesProduccionCientifica(anioInicio, anioFin, universidadId)- start");

    try {
      ServiceType serviceType = ServiceType.PII;
      String relativeUrl = "/invenciones/produccioncientifica/{anioInicio}/{anioFin}/{universidadId}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<InvencionDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<InvencionDto>>() {
          }, anioInicio, anioFin, universidadId).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findInvencionesProduccionCientifica(anioInicio, anioFin, universidadId)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }
}
