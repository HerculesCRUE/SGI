package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformeEvaluacionReportInput implements Serializable {

  @NotNull
  private Long idEvaluacion;

  @NotNull
  private Instant fecha;
}
