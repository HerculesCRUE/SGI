package org.crue.hercules.sgi.rep.dto.csp;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de autorización para participar en proyectos
 * de investigación
 */
public class AutorizacionReport extends SgiReportDto {
  public AutorizacionReport() {
    this.setPath("rep-csp-certificado-autorizacion-proyecto-externo-docx");
    this.setName("autorizacionProyectoExterno");
  }
}