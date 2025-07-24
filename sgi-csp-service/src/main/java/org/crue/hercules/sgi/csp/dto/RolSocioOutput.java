package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.RolSocioNombre;
import org.crue.hercules.sgi.csp.model.RolSocioDescripcion;
import org.crue.hercules.sgi.csp.model.RolSocioAbreviatura;

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
  private Collection<RolSocioNombre> nombre;
  private Collection<RolSocioAbreviatura> abreviatura;
  private Collection<RolSocioDescripcion> descripcion;
  private Boolean coordinador;
  private Boolean activo;
}
