package org.crue.hercules.sgi.rep.service;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.AutorizacionProyectoExternoReportData;
import org.crue.hercules.sgi.rep.service.csp.AutorizacionProyectoExternoService;
import org.crue.hercules.sgi.rep.service.csp.ConvocatoriaService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgempService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe autorizacionproyectoexterno
 */
@Service
@Slf4j
@Validated
public class InformeAutorizacionProyectoExternoReportService extends SgiDocxReportService {

  private final PersonaService personaService;
  private final AutorizacionProyectoExternoService autorizacionProyectoExternoService;
  private final ConvocatoriaService convocatoriaService;
  private final SgiApiSgempService empresaService;

  public InformeAutorizacionProyectoExternoReportService(SgiApiConfService sgiApiConfService,
      PersonaService personaService, AutorizacionProyectoExternoService autorizacionProyectoExternoService,
      ConvocatoriaService convocatoriaService, SgiApiSgempService empresaService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.autorizacionProyectoExternoService = autorizacionProyectoExternoService;
    this.convocatoriaService = convocatoriaService;
    this.empresaService = empresaService;
  }

  public byte[] getReport(Long idAutorizacion) {
    try {
      AutorizacionProyectoExternoReportData reportData = new AutorizacionProyectoExternoReportData();
      AutorizacionDto autorizacionProyectoExterno = autorizacionProyectoExternoService
          .findById(idAutorizacion);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setAutorizacion(autorizacionProyectoExterno);
      if (autorizacionProyectoExterno.getConvocatoriaId() != null) {
        reportData.setConvocatoria(convocatoriaService.findById(autorizacionProyectoExterno.getConvocatoriaId()));
      }
      if (ObjectUtils.isNotEmpty(autorizacionProyectoExterno.getSolicitanteRef())) {
        reportData.setSolicitante(personaService.findById(autorizacionProyectoExterno.getSolicitanteRef()));
        reportData
            .setSolicitanteVinculacion(personaService.getVinculacion(autorizacionProyectoExterno.getSolicitanteRef()));
      }
      if (ObjectUtils.isNotEmpty(autorizacionProyectoExterno.getEntidadRef())) {
        reportData.setEntidad(empresaService.findById(autorizacionProyectoExterno.getEntidadRef()));
      }
      if (ObjectUtils.isNotEmpty(autorizacionProyectoExterno.getResponsableRef())) {
        reportData.setInvestigador(personaService.findById(autorizacionProyectoExterno.getResponsableRef()));
      }
      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }
}