package org.crue.hercules.sgi.prc.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog_;
import org.crue.hercules.sgi.prc.service.BaremacionService;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionLogService;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiTpService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

  /**
   * Crea la tarea programada que lanza el algoritmo de baremación a partir de
   * {@link ConvocatoriaBaremacion}
   * con id indicado.
   *
   * @param convocatoriaBaremacionId id de {@link ConvocatoriaBaremacion}.
   * @return Log
   */
  @PostMapping("/createTask/{convocatoriaBaremacionId}")
  @PreAuthorize("hasAuthority('PRC-CON-BAR')")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public Long createBaremacionTask(@PathVariable Long convocatoriaBaremacionId) {
    log.debug("createBaremacionTask({}, {}) - start", convocatoriaBaremacionId);

    Instant fechaBaremacion = Instant.now().plusSeconds(10);
    return sgiApiTpService.createCallBaremacionTaskId(convocatoriaBaremacionId, fechaBaremacion);
  }

  /**
   * Lanza el algoritmo de baremación a partir de {@link ConvocatoriaBaremacion}
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
   * Resetea las {@link ConvocatoriaBaremacion} que han iniciado la baremación
   * pero han superado el tiempo de finalización
   */
  @PostMapping("/reset")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-prc')) or hasAuthority('PRC-CON-BAR')")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void reset() {
    log.debug("reset() - start");

    convocatoriaBaremacionService.reset();

    log.debug("reset() - end");
  }
}