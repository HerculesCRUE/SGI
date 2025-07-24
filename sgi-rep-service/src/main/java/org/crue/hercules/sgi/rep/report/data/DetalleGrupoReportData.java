package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.data.objects.DetalleGrupoInvestigacionObject;

import com.deepoove.poi.data.PictureRenderData;

public class DetalleGrupoReportData implements ReportData {

  private static final Informe INFORME = Informe.PRC_DETALLE_GRUPO;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setDetalleGrupo(DetalleGrupoInvestigacionOutput value) {
    DetalleGrupoInvestigacionObject detalleGrupoObject = new DetalleGrupoInvestigacionObject(value);
    this.dataReport.put("detalleGrupo", detalleGrupoObject);

    // Values to retain compatibility
    this.dataReport.put("anio", detalleGrupoObject.getAnio());
    this.dataReport.put("grupo", detalleGrupoObject.getGrupo());
    this.dataReport.put("precioPuntoProduccion", detalleGrupoObject.getPrecioPuntoProduccion());
    this.dataReport.put("precioPuntoSexenio", detalleGrupoObject.getPrecioPuntoSexenio());
    this.dataReport.put("precioPuntoCostesIndirectos", detalleGrupoObject.getPrecioPuntoCostesIndirectos());
    this.dataReport.put("investigadores", detalleGrupoObject.getInvestigadores());
    if (detalleGrupoObject.getSexenios() != null) {
      this.dataReport.put("sexeniosNumero", detalleGrupoObject.getSexenios().getNumero());
      this.dataReport.put("sexeniosPuntos", detalleGrupoObject.getSexenios().getPuntos());
      this.dataReport.put("sexeniosImporte", detalleGrupoObject.getSexenios().getImporte());
    }
    this.dataReport.put("produccionesCientificas", detalleGrupoObject.getProduccionesCientificas());
    if (detalleGrupoObject.getCostesIndirectos() != null) {
      this.dataReport.put("costesIndirectosNumero", detalleGrupoObject.getCostesIndirectos().getNumero());
      this.dataReport.put("costesIndirectosPuntos", detalleGrupoObject.getCostesIndirectos().getPuntos());
      this.dataReport.put("costesIndirectosImporte", detalleGrupoObject.getCostesIndirectos().getImporte());
    }
    this.dataReport.put("dineroTotal", detalleGrupoObject.getTotales());
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}
