package org.crue.hercules.sgi.eer.dto;

import java.util.Collection;

import org.crue.hercules.sgi.eer.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.eer.model.TipoDocumentoNombre;

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
  private Collection<TipoDocumentoNombre> nombre;
  private Collection<TipoDocumentoDescripcion> descripcion;
  private TipoDocumentoOutput padre;
  private Boolean activo;
}
