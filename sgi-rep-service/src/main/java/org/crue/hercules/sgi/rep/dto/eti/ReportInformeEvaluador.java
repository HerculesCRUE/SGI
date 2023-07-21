package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe de evaluador
 */
public class ReportInformeEvaluador extends SgiReportDto {
  public ReportInformeEvaluador() {
    this.setPath("rep-eti-ficha-evaluador-docx");
    this.setName("informeEvaluador");
  }
}