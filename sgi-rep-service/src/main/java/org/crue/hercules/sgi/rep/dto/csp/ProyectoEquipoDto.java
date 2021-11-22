package org.crue.hercules.sgi.rep.dto.csp;

import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProyectoEquipoDto extends BaseRestDto {

  private Long proyectoId;
  private String personaRef;
  private PersonaDto persona;
  private RolProyectoDto rolProyecto;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Double horasDedicacion;
  private ProyectoDto proyecto;
}