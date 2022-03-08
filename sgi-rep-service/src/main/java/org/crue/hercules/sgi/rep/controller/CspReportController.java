package org.crue.hercules.sgi.rep.controller;

import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionReport;
import org.crue.hercules.sgi.rep.service.csp.AutorizacionProyectoExternoReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes de CSP
 */
@RestController
@RequestMapping(CspReportController.MAPPING)
@Slf4j
public class CspReportController {
  public static final String MAPPING = "/report/csp";
  private static final OutputType OUTPUT_TYPE_PDF = OutputType.PDF;

  private final AutorizacionProyectoExternoReportService autorizacionProyectoExternoReportService;

  @Autowired
  public CspReportController(AutorizacionProyectoExternoReportService autorizacionProyectoExternoReportService) {
    this.autorizacionProyectoExternoReportService = autorizacionProyectoExternoReportService;
  }

  /**
   * Devuelve un informe de autorización para participar en proyectos de
   * investigación
   *
   * @param idAutorizacion identificador de la Autorización
   * @return Resource
   */
  @GetMapping("/autorizacion-proyecto-externo/{idAutorizacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-B','CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR')")
  public ResponseEntity<Resource> getAutorizacionProyectoExterno(@PathVariable Long idAutorizacion) {

    log.debug("getAutorizacionProyectoExterno(idAutorizacion) - start");

    AutorizacionReport report = new AutorizacionReport();
    report.setOutputType(OUTPUT_TYPE_PDF);

    byte[] reportContent = autorizacionProyectoExternoReportService.getReportAutorizacionProyectoExterno(report,
        idAutorizacion);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getAutorizacionProyectoExterno(idAutorizacion) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

}