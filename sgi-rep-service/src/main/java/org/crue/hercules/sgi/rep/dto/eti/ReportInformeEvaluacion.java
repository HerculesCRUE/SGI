package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe de evaluación
 */
public class ReportInformeEvaluacion extends SgiReportDto {
  public ReportInformeEvaluacion() {
    this.setPath("rep-eti-evaluacion-docx");
    this.setName("informeEvaluacion");
  }
}