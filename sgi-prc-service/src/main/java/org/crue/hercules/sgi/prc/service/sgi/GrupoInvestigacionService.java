package org.crue.hercules.sgi.prc.service.sgi;

import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GrupoInvestigacionService {
  private static final String URL_API = "/TODO";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public GrupoInvestigacionService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Devuelve si personaRef pertenece a un grupo de investigación con un rol con
   * el flag de baremable a true a fecha 31 de diciembre del año que se esta
   * baremando
   *
   * @param personaRef personaRef
   * @param anio       año de baremación
   * @return true/false
   */
  public Boolean isBaremable(String personaRef, Integer anio) {
    return Boolean.TRUE;

    // TODO cuando esté grupos
    // log.debug("isBaremable(personaRef, anio)- start");
    // Boolean isValid = Boolean.FALSE;

    // if (StringUtils.hasText(personaRef) && Objects.nonNull(anio)) {
    // try {
    // StringBuilder url = new StringBuilder();
    // url.append(restApiProperties.getCspUrl());
    // url.append(URL_API);
    // url.append("/TODO/isBaremable");
    // url.append(personaRef);
    // url.append("/");
    // url.append(anio);

    // final ResponseEntity<Boolean> response =
    // restTemplate.exchange(url.toString(), HttpMethod.GET,
    // new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
    // Boolean.class);

    // isValid = response.getBody();
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    // throw new MicroserviceCallException();
    // }
    // }
    // log.debug("isBaremable(personaRef, anio)- end");

    // return isValid;
  }

}
