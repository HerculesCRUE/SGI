package org.crue.hercules.sgi.rep.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.service.SgiReportExcelService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes dinámicos
 */
@RestController
@RequestMapping(CommonReportController.MAPPING)
@Slf4j
public class CommonReportController {
  public static final String MAPPING = "/report/common";

  private final SgiReportExcelService sgiReportExcelService;

  public CommonReportController(SgiReportExcelService sgiReportExcelService) {
    this.sgiReportExcelService = sgiReportExcelService;
  }

  /**
   * Devuelve un informe dinámico
   *
   * @param sgiReport SgiDynamicReportDto con los datos del report
   * @return Resource
   */
  @PostMapping("/dynamic")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Resource> getDynamic(@Valid @RequestBody SgiDynamicReportDto sgiReport) {

    log.debug("getDynamic(SgiDynamicReportDto) - start");
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", sgiReport.getOutputType().getType());
    ByteArrayResource archivo = null;
    try {
      byte[] reportContent = sgiReportExcelService.export(sgiReport);
      archivo = new ByteArrayResource(reportContent);
    } catch (Exception e) {
      log.error("getDynamic(SgiDynamicReportDto) - end", e);
    }
    log.debug("getDynamic(SgiDynamicReportDto) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);

  }

}