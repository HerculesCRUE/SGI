package org.crue.hercules.sgi.csp.dto;

import java.util.Collection;

import org.crue.hercules.sgi.csp.model.GrupoNombre;

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
public class GrupoResumenOutput {
  private Long id;
  private Collection<GrupoNombre> nombre;
  private String codigo;
}
