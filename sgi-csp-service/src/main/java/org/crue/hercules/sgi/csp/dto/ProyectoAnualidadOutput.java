package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

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
public class ProyectoAnualidadOutput implements Serializable {
  private Long id;
  private Integer anio;
  private Long proyectoId;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Boolean presupuestar;
  private Boolean enviadoSge;
}
