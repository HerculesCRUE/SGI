package org.crue.hercules.sgi.rep.service;

import org.crue.hercules.sgi.rep.dto.prc.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.DetalleProduccionInvestigadorReportData;
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
public class InformeDetalleProduccionInvestigadorReportService extends SgiDocxReportService {

  private final SgiApiPrcService sgiApiPrcService;

  public InformeDetalleProduccionInvestigadorReportService(SgiApiConfService sgiApiConfService,
      SgiApiPrcService sgiApiPrcService) {

    super(sgiApiConfService);
    this.sgiApiPrcService = sgiApiPrcService;

  }

  public byte[] getReport(Integer anio, String personaRef) {
    try {
      DetalleProduccionInvestigadorReportData reportData = new DetalleProduccionInvestigadorReportData();
      DetalleProduccionInvestigadorOutput detalleProduccionInvestigador = sgiApiPrcService
          .getDataReportDetalleProduccionInvestigador(anio, personaRef);
      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setDetalleProduccionInvestigador(detalleProduccionInvestigador);

      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }
}