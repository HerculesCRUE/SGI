package org.crue.hercules.sgi.csp.service.sgi;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.eti.ChecklistOutput;
import org.crue.hercules.sgi.csp.dto.eti.EquipoTrabajo;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacion;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiEtiService extends SgiApiBaseService {

  protected SgiApiEtiService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public ChecklistOutput getCheckList(String checklistRef) {
    log.debug("getCheckList(String checklistRef) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/checklists/{checklistRef}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final ChecklistOutput response = super.<ChecklistOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<ChecklistOutput>() {
        }, checklistRef).getBody();

    log.debug("getCheckList(String checklistRef) - end");
    return response;
  }

  public PeticionEvaluacion newPeticionEvaluacion(PeticionEvaluacion peticionEvaluacion) {
    log.debug("newPeticionEvaluacion(PeticionEvaluacion peticionEvaluacion) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final PeticionEvaluacion response = super.<PeticionEvaluacion, PeticionEvaluacion>callEndpoint(mergedURL,
        httpMethod, peticionEvaluacion, new ParameterizedTypeReference<PeticionEvaluacion>() {
        }).getBody();

    log.debug("newPeticionEvaluacion(PeticionEvaluacion peticionEvaluacion) - end");
    return response;
  }

  public PeticionEvaluacion getPeticionEvaluacion(String peticionEvaluacionRef) {
    log.debug("getPeticionEvaluacion(String peticionEvaluacionRef) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones/{peticionEvaluacionRef}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final PeticionEvaluacion response = super.<PeticionEvaluacion>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<PeticionEvaluacion>() {
        }, peticionEvaluacionRef).getBody();

    log.debug("getPeticionEvaluacion(String peticionEvaluacionRef) - end");
    return response;
  }

  public PeticionEvaluacion updatePeticionEvaluacion(String peticionEvaluacionRef,
      PeticionEvaluacion peticionEvaluacion) {
    log.debug("updatePeticionEvaluacion(String peticionEvaluacionRef, PeticionEvaluacion peticionEvaluacion) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones/{peticionEvaluacionRef}";
    HttpMethod httpMethod = HttpMethod.PUT;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final PeticionEvaluacion response = super.<PeticionEvaluacion, PeticionEvaluacion>callEndpoint(mergedURL,
        httpMethod, peticionEvaluacion, new ParameterizedTypeReference<PeticionEvaluacion>() {
        }, peticionEvaluacionRef).getBody();

    log.debug("updatePeticionEvaluacion(String peticionEvaluacionRef, PeticionEvaluacion peticionEvaluacion) - end");
    return response;
  }

  public EquipoTrabajo newEquipoTrabajo(Long id,
      EquipoTrabajo equipoTrabajo) {
    log.debug("newEquipoTrabajo(String peticionEvaluacionRef, EquipoTrabajo equipoTrabajo) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones/{id}/equipos-trabajo";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final EquipoTrabajo response = super.<EquipoTrabajo, EquipoTrabajo>callEndpoint(mergedURL, httpMethod,
        equipoTrabajo, new ParameterizedTypeReference<EquipoTrabajo>() {
        }, id).getBody();

    log.debug("newEquipoTrabajo(String peticionEvaluacionRef, EquipoTrabajo equipoTrabajo) - end");
    return response;
  }
}
