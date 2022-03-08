package org.crue.hercules.sgi.prc.service.sgi;

import java.util.Optional;

import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto;
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
  public Optional<PersonaDto> findById(String personaRef) {
    log.debug("findById(personaRef)- start");
    Optional<PersonaDto> persona = Optional.empty();

    if (StringUtils.hasText(personaRef)) {
      try {
        ServiceType serviceType = ServiceType.SGP;
        String relativeUrl = "/personas/{personaRef}";
        HttpMethod httpMethod = HttpMethod.GET;
        String mergedURL = buildUri(serviceType, relativeUrl);

        final PersonaDto response = super.<PersonaDto>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
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

}
