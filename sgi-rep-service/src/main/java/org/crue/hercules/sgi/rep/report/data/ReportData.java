package org.crue.hercules.sgi.rep.report.data;

import java.util.Map;

import org.crue.hercules.sgi.rep.enums.Informe;

public interface ReportData {
  public Informe getInforme();

  public Map<String, Object> getData();
}
