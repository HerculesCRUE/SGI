package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;

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

  private String nombre;

  private String descripcion;

  private Long padreId;

  private TipoPropiedad tipoPropiedad;

  private Boolean activo;

}
