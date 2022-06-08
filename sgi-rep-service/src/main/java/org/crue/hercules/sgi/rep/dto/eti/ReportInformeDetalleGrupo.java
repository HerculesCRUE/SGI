package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report detalle grupo
 */
public class ReportInformeDetalleGrupo extends SgiReportDto {
  public ReportInformeDetalleGrupo() {
    this.setPath("report/prc/informeDetalleGrupo.prpt");
    this.setName("informeDetalleGrupo");
  }
}