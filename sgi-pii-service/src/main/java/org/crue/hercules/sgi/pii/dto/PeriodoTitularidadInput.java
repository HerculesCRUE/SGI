package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodoTitularidadInput implements Serializable {

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaInicio;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaFin;

  private Long invencionId;

  /** Fecha que se asignará al {@link PeriodoTitularidadInput} vigente anterior */
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaFinPrevious;

}
