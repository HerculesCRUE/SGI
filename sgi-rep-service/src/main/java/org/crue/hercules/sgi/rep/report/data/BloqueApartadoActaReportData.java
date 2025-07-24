package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.data.objects.ActaComentarioObject;

public class BloqueApartadoActaReportData implements ReportData {
  private static final Informe INFORME = Informe.ETI_BLOQUE_APARTADO_ACTA;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setComentarios(List<ActaComentariosMemoriaReportOutput> value) {
    this.dataReport.put("comentariosMemoria", value.stream().map(ActaComentarioObject::new).toList());
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}
