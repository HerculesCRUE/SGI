package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe favorable modificación
 */
public class ReportInformeFavorableModificacion extends SgiReportDto {
  public ReportInformeFavorableModificacion() {
    this.setPath("rep-eti-evaluacion-favorable-memoria-modificacion-prpt");
    this.setName("informeFavorableModificacion");
  }
}