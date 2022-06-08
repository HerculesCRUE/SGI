package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.csp.model.GrupoEquipo.Dedicacion;
import org.crue.hercules.sgi.csp.model.RolProyecto.Equipo;
import org.crue.hercules.sgi.csp.model.RolProyecto.Orden;

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
public class GrupoEquipoOutput implements Serializable {

  private Long id;
  private String personaRef;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Rol rol;
  private Dedicacion dedicacion;
  private BigDecimal participacion;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Rol implements Serializable {
    private Long id;
    private String abreviatura;
    private String nombre;
    private Boolean rolPrincipal;
    private Orden orden;
    private Equipo equipo;
    private Boolean activo;
  }

}
