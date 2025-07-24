package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class ActaComentariosReportOutput {
  private String reportName;
  private Language lang;
  private List<ActaComentariosMemoriaReportOutput> comentariosMemoria;

  public ActaComentariosReportOutput(Language lang) {
    this.reportName = "rep-eti-bloque-apartado-acta-docx-" + lang.getCode();
    this.lang = lang;
  }
}
