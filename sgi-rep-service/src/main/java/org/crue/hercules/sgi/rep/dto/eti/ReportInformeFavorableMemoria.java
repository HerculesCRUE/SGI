package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe favorable memoria
 */
public class ReportInformeFavorableMemoria extends SgiReportDto {
  public ReportInformeFavorableMemoria() {
    this.setPath("rep-eti-evaluacion-favorable-memoria-nueva-prpt");
    this.setName("informeFavorableMemoria");
  }
}