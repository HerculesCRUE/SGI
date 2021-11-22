package org.crue.hercules.sgi.rep.dto.csp;

import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

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
public class ProyectoIvaDto extends BaseRestDto {

  private Long proyectoId;
  private Integer iva;
  private Instant fechaInicio;
  private Instant fechaFin;
  private ProyectoDto proyecto;
}