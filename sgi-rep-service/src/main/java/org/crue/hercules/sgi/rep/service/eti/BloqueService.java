package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoTreeDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiBaseService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BloqueService extends SgiApiBaseService {
  private static final String PATH_DELIMITER = "/";
  public static final String URL_API = PATH_DELIMITER + "bloques";
  private static final String PATH_BLOQUE_COMENTARIOS_GENERALES = URL_API + PATH_DELIMITER + "comentarios-generales";

  private static final String SORT_ORDEN_ASC = "s=orden,asc";

  private static final String PATH_ID = URL_API + PATH_DELIMITER + "{id}";
  private static final String PATH_APARTADOS_TREE = PATH_ID + PATH_DELIMITER + "apartados-tree?" + SORT_ORDEN_ASC;
  private static final String PATH_FORMULARIO = URL_API + PATH_DELIMITER + "{formularioId}" + PATH_DELIMITER
      + "formulario?" + SORT_ORDEN_ASC;

  public BloqueService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<BloqueDto> findByFormularioId(Long idFormulario) {
    log.debug("findByFormularioId({}) - start", idFormulario);
    List<BloqueDto> result = new ArrayList<>();
    try {

      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_FORMULARIO;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<BloqueDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<BloqueDto>>() {
          }, idFormulario).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("findByFormularioId({}) - start", idFormulario);
    return result;
  }

  public BloqueDto getBloqueComentariosGenerales() {
    log.debug("getBloqueComentariosGenerales() - start");
    BloqueDto result = null;
    try {
      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_BLOQUE_COMENTARIOS_GENERALES;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<BloqueDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<BloqueDto>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getBloqueComentariosGenerales() - end");
    return result;
  }

  public List<ApartadoTreeDto> getApartados(Long id) {
    log.debug("getApartados({}) - start", id);
    List<ApartadoTreeDto> result = new ArrayList<>();
    try {

      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_APARTADOS_TREE;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<ApartadoTreeDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<ApartadoTreeDto>>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getApartados({}) - start", id);
    return result;
  }

}
