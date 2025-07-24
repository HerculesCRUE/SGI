package org.crue.hercules.sgi.rep.service;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.EvaluacionFavorableNuevaReportData;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.eti.PeticionEvaluacionService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe favorable memoria de ética
 */
@Service
@Slf4j
public class InformeEvaluacionFavorableNuevaReportService extends SgiDocxReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final PeticionEvaluacionService peticionEvaluacionService;

  public InformeEvaluacionFavorableNuevaReportService(
      SgiApiConfService sgiApiConfService, PersonaService personaService,
      EvaluacionService evaluacionService, PeticionEvaluacionService peticionEvaluacionService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.peticionEvaluacionService = peticionEvaluacionService;
  }

  public byte[] getReport(Long idEvaluacion) {
    try {
      EvaluacionFavorableNuevaReportData reportData = new EvaluacionFavorableNuevaReportData();
      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData
          .setInvestigador(personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef()));
      reportData.setEvaluacion(evaluacion);

      if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTutorRef())) {
        reportData.setTutor(personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getTutorRef()));
      }

      reportData.setEquipo(peticionEvaluacionService
          .getTareasEquipoTrabajo(evaluacion.getMemoria().getPeticionEvaluacion().getId()).stream()
          .filter(tarea -> tarea.getMemoria().getId().equals(evaluacion.getMemoria().getId()))
          .map(tarea -> tarea.getEquipoTrabajo().getPersonaRef())
          .map(personaService::findById).toList());

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