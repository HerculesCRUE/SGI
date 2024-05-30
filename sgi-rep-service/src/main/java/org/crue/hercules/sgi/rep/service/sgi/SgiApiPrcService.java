package org.crue.hercules.sgi.rep.service.sgi;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.rep.dto.prc.ResumenPuntuacionGrupoAnioOutput;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiPrcService extends SgiApiBaseService {

  public SgiApiPrcService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene los datos del informe Detalle grupo con el detalle del
   * reparto de la baremación de una convocatoria de un grupo de investigación. Se
   * muestra el listado de investigadores que forman parte del grupo y los puntos
   * Sexenios, de Costes indirectos y de cada baremo de producción (libros,
   * artículos, trabajos presentados en congresos, dirección de tesis, obras
   * artísticas, comités editoriales, organización de actividades I+D+i, proyectos
   * de investigación, contratos e invenciones).
   *
   * @param anio    Año de la convocatoria
   * @param grupoId Id del grupo
   * @return datos del informe
   */
  public DetalleGrupoInvestigacionOutput getDataReportDetalleGrupo(Integer anio, Long grupoId) {
    log.debug("getDataReportDetalleGrupo({},{}) - start", anio, grupoId);
    DetalleGrupoInvestigacionOutput result = null;

    try {

      ServiceType serviceType = ServiceType.PRC;
      String relativeUrl = "/baremacion/detallegrupo/{anio}/{grupoId}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<DetalleGrupoInvestigacionOutput>callEndpoint(mergedURL,
          httpMethod, new ParameterizedTypeReference<DetalleGrupoInvestigacionOutput>() {
          }, anio, grupoId).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getDataReportDetalleGrupo({},{}) - end", anio, grupoId);

    return result;
  }

  public ResumenPuntuacionGrupoAnioOutput getDataReportResumenPuntuacionGrupos(Integer anio) {
    log.debug("getDataReportResumenPuntuacionGrupos({}) - start", anio);
    ResumenPuntuacionGrupoAnioOutput result = null;

    try {

      ServiceType serviceType = ServiceType.PRC;
      String relativeUrl = "/baremacion/resumenpuntuaciongrupos/{anio}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<ResumenPuntuacionGrupoAnioOutput>callEndpoint(mergedURL,
          httpMethod, new ParameterizedTypeReference<ResumenPuntuacionGrupoAnioOutput>() {
          }, anio).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getDataReportResumenPuntuacionGrupos({}) - end", anio);

    return result;
  }

  public DetalleProduccionInvestigadorOutput getDataReportDetalleProduccionInvestigador(Integer anio,
      String personaRef) {
    log.debug("getDataReportDetalleProduccionInvestigador({}, {}) - start", anio, personaRef);
    DetalleProduccionInvestigadorOutput result = null;

    try {

      ServiceType serviceType = ServiceType.PRC;
      String relativeUrl = "/baremacion/detalleproduccioninvestigador/{anio}/{personaRef}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<DetalleProduccionInvestigadorOutput>callEndpoint(mergedURL,
          httpMethod, new ParameterizedTypeReference<DetalleProduccionInvestigadorOutput>() {
          }, anio, personaRef).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("getDataReportDetalleProduccionInvestigador({}, {}) - end", anio, personaRef);

    return result;
  }

}
