package org.crue.hercules.sgi.rep.controller;

import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeDetalleGrupo;
import org.crue.hercules.sgi.rep.service.prc.InformeDetalleGrupoReportService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes de PRC
 */
@RestController
@RequestMapping(PrcReportController.MAPPING)
@RequiredArgsConstructor
@Slf4j
public class PrcReportController {
  public static final String MAPPING = "/report/prc";
  private static final OutputType OUTPUT_TYPE_PDF = OutputType.PDF;

  private final InformeDetalleGrupoReportService informeDetalleGrupoReportService;

  /**
   * Devuelve un informe de detalle de grupo de investigación
   *
   * @param anio     Año de la convocatoria
   * @param grupoRef Identificador del grupo de investigación
   * @return Resource
   */
  @GetMapping("/informedetallegrupo/{anio}/{grupoRef}")
  @PreAuthorize("hasAuthority('PRC-INF-G')")
  public ResponseEntity<Resource> getInformeDetalleGrupo(@PathVariable Integer anio, @PathVariable Long grupoRef) {

    log.debug("getInformeDetalleGrupo({}, {}) - start", anio, grupoRef);

    ReportInformeDetalleGrupo report = new ReportInformeDetalleGrupo();
    report.setOutputType(OUTPUT_TYPE_PDF);

    byte[] reportContent = informeDetalleGrupoReportService.getReportDetalleGrupo(report, anio, grupoRef);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeDetalleGrupo({}, {}) - end", anio, grupoRef);
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

}