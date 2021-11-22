package org.crue.hercules.sgi.rep.dto.eti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TipoInvestigacionTuteladaDto {

  private Long id;
  private String nombre;
  private Boolean activo;

}