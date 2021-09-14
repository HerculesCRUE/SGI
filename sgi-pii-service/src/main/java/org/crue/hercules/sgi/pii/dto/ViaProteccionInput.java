package org.crue.hercules.sgi.pii.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.ViaProteccion;

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
public class ViaProteccionInput {

  @NotEmpty
  @Size(max = ViaProteccion.NOMBRE_LENGTH)
  private String nombre;

  @NotEmpty
  @Size(max = ViaProteccion.DESCRIPCION_LENGTH)
  private String descripcion;

  @NotNull
  private TipoPropiedad tipoPropiedad;

  Integer mesesPrioridad;

  Boolean paisEspecifico;

  Boolean extensionInternacional;

  Boolean variosPaises;

}
