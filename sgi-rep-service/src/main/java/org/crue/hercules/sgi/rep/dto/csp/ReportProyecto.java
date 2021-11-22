package org.crue.hercules.sgi.rep.dto.csp;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Instancia que contiene un report de proyectos
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@SuperBuilder
public class ReportProyecto extends SgiReportDto {

  private String query;
  private Pageable paging;
  private Boolean exportAsSubReport;

  public ReportProyecto() {
    this.setPath("report/common/dynamic.prpt");
    this.setName("proyecto");
  }
}