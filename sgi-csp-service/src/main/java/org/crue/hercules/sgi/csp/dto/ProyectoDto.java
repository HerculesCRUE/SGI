package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProyectoDto implements Serializable {

  private Long id;
  private String titulo;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaFinDefinitiva;
  private Boolean contrato;
  private BigDecimal totalImporteConcedido;
  private BigDecimal importeConcedidoCostesIndirectos;
  private Long ambitoGeograficoId;
  private Boolean convocatoriaExcelencia;
}
