package org.crue.hercules.sgi.rep.controller;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.csp.ReportProyecto;
import org.crue.hercules.sgi.rep.service.csp.InformeProyectoReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes de ETI
 */
@RestController
@RequestMapping(CspReportController.MAPPING)
@Slf4j
public class CspReportController {
  public static final String MAPPING = "/report/csp";

  private final InformeProyectoReportService informeProyectoReportService;

  public CspReportController(InformeProyectoReportService informeProyectoReportService) {
    this.informeProyectoReportService = informeProyectoReportService;
  }

  /**
   * Devuelve un informe genérico de proyectos
   * 
   * @param query            Filtros de la consulta
   * @param paging           Valores de paginación
   * @param outputReportType Tipo de salida del informe
   * @return Resource
   */
  @GetMapping("/proyecto")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Resource> getProyecto(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging, OutputReportType outputReportType) {

    log.debug("getProyecto(query, paging, outputReportType) - start");

    ReportProyecto report = new ReportProyecto();
    report.setQuery(query);
    report.setPaging(paging);
    report.setOutputReportType(outputReportType);
    report.setExportAsSubReport(Boolean.TRUE);

    SgiDynamicReportDto sgiDynamicReportDto = informeProyectoReportService.getReport(report);
    ByteArrayResource archivo = new ByteArrayResource(sgiDynamicReportDto.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, sgiDynamicReportDto.getOutputReportType().getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

}