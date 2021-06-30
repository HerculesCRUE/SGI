package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

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
public class SectorAplicacionOutput implements Serializable {
  private Long id;
  private String nombre;
  private String descripcion;
  private Boolean activo;
}
