package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

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
public class AnualidadIngresoInput implements Serializable {
  @NotNull
  private Long anualidadId;

  private String codigoEconomicoRef;

  private Long proyectoPartidaId;

  private BigDecimal importeConcedido;

  private String proyectoSgeRef;

}
