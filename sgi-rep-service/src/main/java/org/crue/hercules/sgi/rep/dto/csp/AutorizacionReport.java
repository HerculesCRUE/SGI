package org.crue.hercules.sgi.rep.dto.csp;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de autorización para participar en proyectos
 * de investigación
 */
public class AutorizacionReport extends SgiReportDto {
  public AutorizacionReport() {
    this.setPath("report/csp/autorizacionProyectoExterno.prpt");
    this.setName("autorizacionProyectoExterno");
  }
}