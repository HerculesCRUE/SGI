package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.prc.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.rep.enums.Informe;

import com.deepoove.poi.data.PictureRenderData;

public class DetalleProduccionInvestigadorReportData implements ReportData {

  private static final Informe INFORME = Informe.PRC_DETALLE_PRODUCCION_INVESTIGADOR;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setDetalleProduccionInvestigador(DetalleProduccionInvestigadorOutput value) {
    this.dataReport.put("detalleProduccionInvestigador", value);

    // Values to retain compatibility
    dataReport.put("anio", value.getAnio());
    dataReport.put("investigador", value.getInvestigador());
    dataReport.put("producciones", value.getTipos());
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}
