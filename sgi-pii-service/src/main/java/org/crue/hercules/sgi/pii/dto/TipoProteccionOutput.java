package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.TipoProteccionDescripcion;
import org.crue.hercules.sgi.pii.model.TipoProteccionNombre;

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
public class TipoProteccionOutput implements Serializable {

  private Long id;

  private Collection<TipoProteccionNombre> nombre;

  private Collection<TipoProteccionDescripcion> descripcion;

  private Long padreId;

  private TipoPropiedad tipoPropiedad;

  private Boolean activo;

}
