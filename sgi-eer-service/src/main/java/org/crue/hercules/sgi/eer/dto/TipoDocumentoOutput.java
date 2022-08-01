package org.crue.hercules.sgi.eer.dto;

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
public class TipoDocumentoOutput {
  private Long id;
  private String nombre;
  private String descripcion;
  private TipoDocumentoOutput padre;
  private Boolean activo;
}
