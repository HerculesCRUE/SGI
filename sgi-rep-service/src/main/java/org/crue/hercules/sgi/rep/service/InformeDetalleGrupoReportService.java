package org.crue.hercules.sgi.rep.service;

import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.DetalleGrupoReportData;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiPrcService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe detalle grupo de PRC
 */
@Service
@Slf4j
@Validated
public class InformeDetalleGrupoReportService extends SgiDocxReportService {

  private final SgiApiPrcService sgiApiPrcService;

  public InformeDetalleGrupoReportService(SgiApiConfService sgiApiConfService,
      SgiApiPrcService sgiApiPrcService) {

    super(sgiApiConfService);
    this.sgiApiPrcService = sgiApiPrcService;

  }

  public byte[] getReport(Integer anio, Long grupoId) {
    try {
      DetalleGrupoReportData reportData = new DetalleGrupoReportData();
      DetalleGrupoInvestigacionOutput detalleGrupo = sgiApiPrcService.getDataReportDetalleGrupo(anio, grupoId);
      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setDetalleGrupo(detalleGrupo);

      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }
}