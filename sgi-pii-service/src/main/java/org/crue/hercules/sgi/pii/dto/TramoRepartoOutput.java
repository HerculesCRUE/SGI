package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.pii.model.TramoReparto.Tipo;

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
public class TramoRepartoOutput implements Serializable {
  private Long id;
  private Integer desde;
  private Integer hasta;
  private BigDecimal porcentajeUniversidad;
  private BigDecimal porcentajeInventores;
  private Tipo tipo;
}
