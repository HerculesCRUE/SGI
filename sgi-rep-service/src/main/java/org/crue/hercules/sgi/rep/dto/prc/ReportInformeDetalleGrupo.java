package org.crue.hercules.sgi.rep.dto.prc;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report detalle grupo
 */
public class ReportInformeDetalleGrupo extends SgiReportDto {
  public ReportInformeDetalleGrupo() {
    this.setPath("rep-prc-detalle-grupo-prpt");
    this.setName("informeDetalleGrupo");
  }
}