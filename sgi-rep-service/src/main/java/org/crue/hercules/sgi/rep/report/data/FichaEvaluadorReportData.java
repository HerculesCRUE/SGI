package org.crue.hercules.sgi.rep.report.data;

import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.EvaluacionObject;
import org.crue.hercules.sgi.rep.report.data.objects.PersonaObject;

import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.PictureRenderData;

public class FichaEvaluadorReportData implements ReportData {
  private static final Informe INFORME = Informe.ETI_FICHA_EVALUADOR;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setEvaluacion(EvaluacionDto value) {
    EvaluacionObject evaluacionObject = new EvaluacionObject(value);

    this.dataReport.put("evaluacion", evaluacionObject);

    // Added to direct access nested objects
    this.dataReport.put("convocatoria", evaluacionObject.getConvocatoria());
    this.dataReport.put("memoria", evaluacionObject.getMemoria());
    this.dataReport.put("peticionEvaluacion", evaluacionObject.getMemoria().getPeticionEvaluacion());

    // Valores retenidos por compatibilidad
    this.dataReport.put("referenciaMemoria", evaluacionObject.getMemoria().getNumReferencia());
    this.dataReport.put("tipoActividad",
        evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().toString());

    this.dataReport.put("titulo", evaluacionObject.getMemoria().getPeticionEvaluacion().getTitulo());
    this.dataReport.put("fechaInicio",
        SgiReportHelper.formatDate(evaluacionObject.getMemoria().getPeticionEvaluacion().getFechaInicio(),
            FormatStyle.SHORT.name()));
    this.dataReport.put("fechaFin", SgiReportHelper
        .formatDate(evaluacionObject.getMemoria().getPeticionEvaluacion().getFechaFin(),
            FormatStyle.SHORT.name()));
    this.dataReport.put("financiacion", evaluacionObject.getMemoria().getPeticionEvaluacion().getFuenteFinanciacion());
    this.dataReport.put("resumen", evaluacionObject.getMemoria().getPeticionEvaluacion().getResumen());

    this.dataReport.put("comite", evaluacionObject.getMemoria().getComite().getCodigo());
    this.dataReport.put("nombreInvestigacion", evaluacionObject.getMemoria().getComite().getNombreInvestigacion());
  }

  public void setSolicitante(PersonaDto value) {
    PersonaObject personaObject = new PersonaObject(value);
    this.dataReport.put("solicitante", personaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("nombreResponsable", value.getNombre() + " " + value.getApellidos());
  }

  public void setBloqueApartados(DocxRenderData value) {
    this.dataReport.put("bloqueApartados", value);
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}
