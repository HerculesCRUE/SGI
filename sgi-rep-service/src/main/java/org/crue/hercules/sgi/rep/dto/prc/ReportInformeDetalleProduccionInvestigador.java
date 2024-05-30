package org.crue.hercules.sgi.rep.dto.prc;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report resumen puntuación grupo
 */
public class ReportInformeDetalleProduccionInvestigador extends SgiReportDto {
  public ReportInformeDetalleProduccionInvestigador() {
    this.setPath("rep-prc-detalle-produccion-investigador-docx");
    this.setName("informeDetalleProduccionInvestigador");
  }
}