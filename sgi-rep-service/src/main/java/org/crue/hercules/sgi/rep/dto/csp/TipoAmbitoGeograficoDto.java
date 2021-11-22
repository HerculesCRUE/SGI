package org.crue.hercules.sgi.rep.dto.csp;

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
public class TipoAmbitoGeograficoDto extends BaseRestDto {

  private Long id;
  private String nombre;
  private Boolean activo;
}