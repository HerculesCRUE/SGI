package org.crue.hercules.sgi.csp.dto;

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
public class RolSocioOutput implements Serializable {
  private Long id;
  private String nombre;
  private String abreviatura;
  private String descripcion;
  private Boolean coordinador;
  private Boolean activo;
}
