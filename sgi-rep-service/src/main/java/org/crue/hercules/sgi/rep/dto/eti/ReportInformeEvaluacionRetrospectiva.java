package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe evaluación retrospectiva
 */
public class ReportInformeEvaluacionRetrospectiva extends SgiReportDto {
  public ReportInformeEvaluacionRetrospectiva() {
    this.setPath("rep-eti-evaluacion-retrospectiva-prpt");
    this.setName("informeEvaluacionRetrospectiva");
  }
}