package org.crue.hercules.sgi.rep.service;

import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.EvaluacionRetrospectivaReportData;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe evaluación retrospectiva de ética
 */
@Service
@Slf4j
public class InformeEvaluacionRetrospectivaReportService extends SgiDocxReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;

  public InformeEvaluacionRetrospectivaReportService(
      SgiApiConfService sgiApiConfService,
      PersonaService personaService, EvaluacionService evaluacionService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
  }

  public byte[] getReport(Long idEvaluacion) {
    try {
      EvaluacionRetrospectivaReportData reportData = new EvaluacionRetrospectivaReportData();

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setEvaluacion(evaluacion);
      reportData
          .setInvestigador(personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef()));
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