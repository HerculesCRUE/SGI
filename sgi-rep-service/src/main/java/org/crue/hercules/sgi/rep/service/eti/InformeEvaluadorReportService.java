package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluador;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe de evaluador de ética
 */
@Service
@Validated
public class InformeEvaluadorReportService extends InformeEvaluacionEvaluadorBaseReportService {

  public InformeEvaluadorReportService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService,
      PersonaService personaService, EvaluacionService evaluacionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, baseApartadosRespuestasService);
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    dataReport.put("referenciaMemoria", evaluacion.getMemoria().getNumReferencia());
    dataReport.put("version", evaluacion.getVersion());

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), dataReport, "Responsable");

    String tipoActividad = "";
    if (null != evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada() && StringUtils
        .hasText(evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre())) {
      tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
    } else if (null != evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad()
        && StringUtils.hasText(evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre())) {
      tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre();
    }
    dataReport.put("tipoActividad", tipoActividad);
    dataReport.put("titulo", evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());
    dataReport.put("fechaInicio",
        formatInstantToString(evaluacion.getMemoria().getPeticionEvaluacion().getFechaInicio()));
    dataReport.put("fechaFin", formatInstantToString(evaluacion.getMemoria().getPeticionEvaluacion().getFechaFin()));
    dataReport.put("financiacion", evaluacion.getMemoria().getPeticionEvaluacion().getFuenteFinanciacion());
    dataReport.put("resumen", evaluacion.getMemoria().getPeticionEvaluacion().getResumen());

    dataReport.put("comite", evaluacion.getMemoria().getComite().getComite());
    dataReport.put("nombreInvestigacion", evaluacion.getMemoria().getComite().getNombreInvestigacion());

    Long dictamenId = evaluacion.getDictamen() != null ? evaluacion.getDictamen().getId() : null;

    dataReport.put("bloqueApartados", generarBloqueApartados(dictamenId, getInformeEvaluador(evaluacion.getId()),
        "rep-eti-bloque-apartado-ficha-evaluador-docx"));

    return compileReportData(path, dataReport);
  }

  private XWPFDocument getReportFromEvaluador(SgiReportDto sgiReport, Long idEvaluacion) {
    return this.getReportFromEvaluacionId(sgiReport, idEvaluacion);

  }

  public byte[] getReportInformeEvaluadorEvaluacion(ReportInformeEvaluador sgiReport, Long idEvaluacion) {
    this.getReportFromEvaluador(sgiReport, idEvaluacion);
    return sgiReport.getContent();
  }
}