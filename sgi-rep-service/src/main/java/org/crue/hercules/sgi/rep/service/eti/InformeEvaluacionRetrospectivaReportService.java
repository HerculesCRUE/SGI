package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacionRetrospectiva;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe evaluación retrospectiva de ética
 */
@Service
@Validated
public class InformeEvaluacionRetrospectivaReportService extends InformeEvaluacionEvaluadorBaseReportService {

  public InformeEvaluacionRetrospectivaReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService,
      PersonaService personaService, EvaluacionService evaluacionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, baseApartadosRespuestasService);
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(),
        dataReport);
    dataReport.put("tituloProyecto", evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    dataReport.put("lugar", evaluacion.getConvocatoriaReunion().getNumeroActa());

    dataReport.put("nombreInvestigacion", evaluacion.getMemoria().getComite().getNombreInvestigacion());

    dataReport.put("comite", evaluacion.getMemoria().getComite().getComite());

    addDataEvaluacion(evaluacion, dataReport);

    return compileReportData(path, dataReport);
  }

  public byte[] getReportInformeEvaluacionRetrospectiva(ReportInformeEvaluacionRetrospectiva sgiReport,
      InformeEvaluacionReportInput input) {
    getReportFromEvaluacionId(sgiReport, input.getIdEvaluacion());
    return sgiReport.getContent();
  }

}