package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class InformeEvaluacionEvaluadorReportOutput extends BloquesReportOutput {
  private String reportName;
  private EvaluacionDto evaluacion;

  public InformeEvaluacionEvaluadorReportOutput(String reportName, Language lang) {
    super(lang);
    this.reportName = reportName + "-" + lang.getCode();
  }
}
