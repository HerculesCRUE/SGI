package org.crue.hercules.sgi.prc.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.prc.dto.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.prc.dto.ResumenPuntuacionGrupoAnioOutput;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog_;
import org.crue.hercules.sgi.prc.service.BaremacionService;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionLogService;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaReportService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiTpService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BaremacionController
 */
@RestController
@RequestMapping(BaremacionController.MAPPING)
@Slf4j
@RequiredArgsConstructor
public class BaremacionController {
  public static final String MAPPING = "/baremacion";

  private final BaremacionService service;
  private final SgiConfigProperties sgiConfigProperties;
  private final ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService;
  private final ConvocatoriaBaremacionService convocatoriaBaremacionService;
  private final SgiApiTpService sgiApiTpService;
  private final ProduccionCientificaReportService produccionCientificaReportService;

  /**
   * Crea la tarea programada que lanza el algoritmo de baremaci??n a partir de
   * {@link ConvocatoriaBaremacion}
   * con id indicado.
   *
   * @param convocatoriaBaremacionId id de {@link ConvocatoriaBaremacion}.
   * @return Log
   */
  @PostMapping("/createTask/{convocatoriaBaremacionId}")
  @PreAuthorize("hasAuthority('PRC-CON-BAR')")
  @ResponseStatus(value = HttpStatus.OK)
  public Long createBaremacionTask(@PathVariable Long convocatoriaBaremacionId) {
    log.debug("createBaremacionTask({}) - start", convocatoriaBaremacionId);

    service.checkInitBaremacion(convocatoriaBaremacionId);

    convocatoriaBaremacionService.initFechasBaremacion(convocatoriaBaremacionId);

    Instant fechaBaremacion = Instant.now().plusSeconds(10);
    return sgiApiTpService.createCallBaremacionTaskId(convocatoriaBaremacionId, fechaBaremacion);
  }

  /**
   * Lanza el algoritmo de baremaci??n a partir de {@link ConvocatoriaBaremacion}
   * con id indicado.
   *
   * @param convocatoriaBaremacionId id de {@link ConvocatoriaBaremacion}.
   * @return Log
   */
  @PostMapping("/{convocatoriaBaremacionId}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-prc')) or hasAuthority('PRC-CON-BAR')")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public String baremacion(@PathVariable Long convocatoriaBaremacionId) {
    log.debug("baremacion({}) - start", convocatoriaBaremacionId);

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    service.baremacion(convocatoriaBaremacionId);

    // TODO quitar despues de pruebas

    DateTimeFormatter dfDateTimeOut = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    String strFechaActual = dfDateTimeOut.format(fechaActual);

    PageRequest page = PageRequest.of(0, 10000, Sort.by(Sort.Direction.ASC, ConvocatoriaBaremacionLog_.ID));

    return convocatoriaBaremacionLogService.findAll(Auditable_.CREATION_DATE + "=ge=" + strFechaActual, page)
        .getContent().stream()
        .map(ConvocatoriaBaremacionLog::getTrace)
        .collect(Collectors.joining("\n"));
  }

  /**
   * Resetea las {@link ConvocatoriaBaremacion} que han iniciado la baremaci??n
   * pero han superado el tiempo de finalizaci??n
   */
  @PostMapping("/reset")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-prc')) or hasAuthority('PRC-CON-BAR')")
  @ResponseStatus(value = HttpStatus.OK)
  public void reset() {
    log.debug("reset() - start");

    convocatoriaBaremacionService.reset();

    log.debug("reset() - end");
  }

  /**
   * REPORT
   */

  /**
   * Obtiene los datos del informe Resumen puntuaci??n grupos con las puntuaciones
   * globales de cada uno de los grupos de investigaci??n que a fecha 31 de
   * diciembre del a??o pasado como par??metro est??n en estado activo y tengan el
   * campo "especial investigaci??n" a valor "No".
   *
   * @param anio A??o de la convocatoria
   * @return datos del informe
   */
  @GetMapping("/resumenpuntuaciongrupos/{anio}")
  @PreAuthorize("hasAuthority('PRC-INF-G')")
  @ResponseStatus(value = HttpStatus.OK)
  public ResumenPuntuacionGrupoAnioOutput getDataReportResumenPuntuacionGrupos(@PathVariable Integer anio) {
    log.debug("getDataReportResumenPuntuacionGrupos({}) - start", anio);

    return produccionCientificaReportService.getDataReportResumenPuntuacionGrupos(anio);
  }

  /**
   * Obtiene los datos del informe Detalle producci??n investigador con el
   * detalle de los puntos obtenidos en cada item de producci??n cient??fica para un
   * investigador concreto. En los puntos de producci??n cient??fica se debe de
   * detallar los puntos de cada uno de los tipos y los items en concreto que han
   * dado lugar a la puntuaci??n obtenida.
   *
   * @param anio       A??o de la convocatoria
   * @param personaRef Id del investigador
   * @return datos del informe
   */
  @GetMapping("/detalleproduccioninvestigador/{anio}/{personaRef}")
  @PreAuthorize("hasAuthority('PRC-INF-G')")
  @ResponseStatus(value = HttpStatus.OK)
  public DetalleProduccionInvestigadorOutput getDataDetalleProduccionInvestigador(@PathVariable Integer anio,
      @PathVariable String personaRef) {
    log.debug("getDataDetalleProduccionInvestigador({},{}) - start", anio, personaRef);

    return produccionCientificaReportService.getDataDetalleProduccionInvestigador(anio, personaRef);
  }

  /**
   * Obtiene los datos del informe Detalle grupo con el detalle del
   * reparto de la baremaci??n de una convocatoria de un grupo de investigaci??n. Se
   * muestra el listado de investigadores que forman parte del grupo y los puntos
   * Sexenios, de Costes indirectos y de cada baremo de producci??n (libros,
   * art??culos, trabajos presentados en congresos, direcci??n de tesis, obras
   * art??sticas, comit??s editoriales, organizaci??n de actividades I+D+i, proyectos
   * de investigaci??n, contratos e invenciones).
   *
   * @param anio    A??o de la convocatoria
   * @param grupoId Id del grupo
   * @return datos del informe
   */
  @GetMapping("/detallegrupo/{anio}/{grupoId}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-prc')) or hasAuthority('PRC-INF-G')")
  @ResponseStatus(value = HttpStatus.OK)
  public DetalleGrupoInvestigacionOutput getDataReportDetalleGrupo(@PathVariable Integer anio,
      @PathVariable Long grupoId) {
    log.debug("getDataReportDetalleGrupo({},{}) - start", anio, grupoId);

    return produccionCientificaReportService.getDataReportDetalleGrupo(anio, grupoId);
  }
}