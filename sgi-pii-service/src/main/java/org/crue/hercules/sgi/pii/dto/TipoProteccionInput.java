package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion.TipoPropiedad;

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
public class TipoProteccionInput implements Serializable {

  @NotEmpty
  @Size(max = TipoProteccion.NOMBRE_LENGTH)
  private String nombre;

  @NotEmpty
  @Size(max = TipoProteccion.DESCRIPCION_LENGTH)
  private String descripcion;

  @NotNull
  private TipoPropiedad tipoPropiedad;

  private Long padreId;

}
