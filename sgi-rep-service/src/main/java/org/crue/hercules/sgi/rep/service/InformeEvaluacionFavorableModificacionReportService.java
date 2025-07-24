package org.crue.hercules.sgi.rep.service;

import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.EvaluacionFavorableModificacionReportData;
import org.crue.hercules.sgi.rep.service.eti.ConvocatoriaReunionService;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe favorable modificación de ética
 */
@Service
@Slf4j
public class InformeEvaluacionFavorableModificacionReportService extends SgiDocxReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final ConvocatoriaReunionService convocatoriaReunionService;

  public InformeEvaluacionFavorableModificacionReportService(
      SgiApiConfService sgiApiConfService, PersonaService personaService, EvaluacionService evaluacionService,
      ConvocatoriaReunionService convocatoriaReunionService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.convocatoriaReunionService = convocatoriaReunionService;
  }

  public byte[] getReport(Long idEvaluacion) {
    try {
      EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();
      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData
          .setInvestigador(personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef()));
      reportData.setEvaluacion(evaluacion);
      reportData.setConvocatoriaUltimaEvaluacion(convocatoriaReunionService
          .findConvocatoriaUltimaEvaluacionTipoMemoria(evaluacion.getId(), evaluacion.getDictamen().getId()));
      reportData.setFechaPrimerEnvioSecretaria(
          evaluacionService.findFirstFechaEnvioSecretariaByIdEvaluacion(evaluacion.getId()));
      if (evaluacionService.findSecretarioEvaluacion(evaluacion.getId()) != null) {
        reportData.setSecretario(
            personaService.findById(evaluacionService.findSecretarioEvaluacion(evaluacion.getId()).getPersonaRef()));
      }
      if (evaluacionService.findIdPresidenteByIdEvaluacion(evaluacion.getId()) != null) {
        reportData.setPresidente(
            personaService.findById(evaluacionService.findIdPresidenteByIdEvaluacion(evaluacion.getId())));
      }

      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

}