package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.data.objects.BloqueApartadoObject;

public class BloqueApartadoFichaEvaluadorReportData implements ReportData {
  private static final Informe INFORME = Informe.ETI_BLOQUE_APARTADO_FICHA_EVALUADOR;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setIdDictamen(Long value) {
    this.dataReport.put("idDictamen", value);
  }

  public void setIdDictamenNoProcedeEvaluar(Long value) {
    this.dataReport.put("idDictamenNoProcedeEvaluar", value);
  }

  public void setNumComentarios(Integer value) {
    this.dataReport.put("numComentarios", value);
  }

  public void setBloques(List<BloqueOutput> value) {
    this.dataReport.put("bloques", value.stream().map(BloqueApartadoObject::new).toList());
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }

}
