package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.pii.model.Reparto.Estado;
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
public class RepartoInput implements Serializable {
  @NotNull
  private Long invencionId;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fecha;

  @Min(0)
  @NotNull
  private BigDecimal importeUniversidad;

  @Min(0)
  private BigDecimal importeEquipoInventor;

  @NotNull
  private Estado estado;
}
