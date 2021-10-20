package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.dto.EtiMXXReportOutput;
import org.crue.hercules.sgi.eti.service.EtiReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes de ETI
 * 
 */
@RestController
@RequestMapping(EtiReportController.MAPPING)
@Slf4j
public class EtiReportController {
  public static final String MAPPING = "/reports";

  private final EtiReportService etiReportService;

  /**
   * Instancia un nuevo EtiReportController.
   * 
   * @param etiReportService {@link EtiReportService}.
   */
  public EtiReportController(EtiReportService etiReportService) {
    this.etiReportService = etiReportService;
  }

  /**
   * Devuelve los datos necesarios para generar un informe M10, M20 o M30
   *
   * @param idMemoria Identificador de la memoria
   * @return EtiMXXReportOutput
   */
  @GetMapping("/mxx/{idMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-INV-ER')")
  EtiMXXReportOutput getMXX(@PathVariable Long idMemoria) {

    log.debug("getMXX(idMemoria) - start");

    EtiMXXReportOutput reportOutput = etiReportService.getDataReportMXX(idMemoria);

    log.debug("getMXX(idMemoria) - end");
    return reportOutput;
  }

}