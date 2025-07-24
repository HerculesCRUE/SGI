package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BloquesReportOutput {
  Language lang;
  private List<BloqueOutput> bloques;
  private EvaluacionDto evaluacion;

  public BloquesReportOutput(Language lang) {
    this.lang = lang;
  }
}
