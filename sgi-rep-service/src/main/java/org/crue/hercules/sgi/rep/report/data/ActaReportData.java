package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.ActaObject;
import org.crue.hercules.sgi.rep.report.data.objects.AsistenteObject;
import org.crue.hercules.sgi.rep.report.data.objects.MemoriaEvaluadaObject;

import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.PictureRenderData;

public class ActaReportData implements ReportData {

  private static final Informe INFORME = Informe.ETI_ACTA;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setActa(ActaDto value) {
    ActaObject actaObject = new ActaObject(value);
    this.dataReport.put("acta", actaObject);

    // Add quick access to Acta nested Values
    this.dataReport.put("convocatoria", actaObject.getConvocatoria());

    // Values to retain compatibility
    this.dataReport.put("fecha",
        SgiReportHelper.formatDate(actaObject.getConvocatoria().getFechaEvaluacion(),
            String.format("EEEE dd '%s' MMMM '%s' yyyy", ApplicationContextSupport.getMessage("common.de"),
                ApplicationContextSupport.getMessage("common.de"))));
    this.dataReport.put("numeroActa", actaObject.getNumero());
    this.dataReport.put("comite", actaObject.getConvocatoria().getComite().getCodigo());
    this.dataReport.put("nombreInvestigacion", actaObject.getConvocatoria().getComite().getNombreInvestigacion());
    this.dataReport.put("fechaConvocatoria",
        SgiReportHelper.formatDate(actaObject.getConvocatoria().getFechaEvaluacion(),
            String.format("dd '%s' MMMM '%s' yyyy", ApplicationContextSupport.getMessage("common.de"),
                ApplicationContextSupport.getMessage("common.de"))));
    this.dataReport.put("lugar", actaObject.getConvocatoria().getLugar());
    this.dataReport.put("isVideoconferencia", actaObject.getConvocatoria().getVideoConferencia());
    this.dataReport.put("horaInicio", SgiReportHelper.formatTime(actaObject.getInicio(), "SHORT"));
    this.dataReport.put("horaFin", SgiReportHelper.formatTime(actaObject.getFin(), "SHORT"));
    if (actaObject.getDuracion().toHours() > 0) {
      this.dataReport.put("duracion", DurationFormatUtils.formatDuration(actaObject.getDuracion().toMillis(),
          SgiReportHelper.getMessage("duration.format.hours-minutes"), false));
    } else {
      this.dataReport.put("duracion", DurationFormatUtils.formatDuration(actaObject.getDuracion().toMillis(),
          SgiReportHelper.getMessage("duration.format.minutes"), false));
    }
    this.dataReport.put("tipoConvocatoria", actaObject.getConvocatoria().getTipo());
    this.dataReport.put("resumenActa", actaObject.getResumen());
    this.dataReport.put("codigoActa", actaObject.getCodigo());
    this.dataReport.put("ordenDelDia", actaObject.getConvocatoria().getOrdenDia());

  }

  public void setExistComentarios(Boolean value) {
    this.dataReport.put("existsComentarios", value);
  }

  public void setNumEvaluacionesNuevas(Long value) {
    this.dataReport.put("numeroEvaluacionesNuevas", value != null ? value : 0);
  }

  public void setNumEvaluacionesRevisioSinMinima(Long value) {
    this.dataReport.put("numeroEvaluacionesRevisiones", value != null ? value : 0);
  }

  public void setAsistentes(List<AsistenteObject> value) {
    this.dataReport.put("asistentes", value);
  }

  public void setMemoriasEvaluadas(List<MemoriaEvaluadaObject> value) {
    this.dataReport.put("isMemoriasEvaluadas", !value.isEmpty());
    this.dataReport.put("memoriasEvaluadas", value);
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
