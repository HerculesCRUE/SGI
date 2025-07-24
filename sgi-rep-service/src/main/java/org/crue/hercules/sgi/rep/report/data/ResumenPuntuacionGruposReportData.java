package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.prc.ResumenPuntuacionGrupoAnioOutput;
import org.crue.hercules.sgi.rep.enums.Informe;

import com.deepoove.poi.data.PictureRenderData;

public class ResumenPuntuacionGruposReportData implements ReportData {

  private static final Informe INFORME = Informe.PRC_RESUMEN_PUNTUACION_GRUPOS;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setResumen(ResumenPuntuacionGrupoAnioOutput value) {
    this.dataReport.put("resumenPuntuacionGrupos", value);

    // Values to retain compatibility
    if (value != null) {
      this.dataReport.put("anio", value.getAnio());
      this.dataReport.put("puntuacionesGrupos", value.getPuntuacionesGrupos());
    }
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}
