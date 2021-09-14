package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class ViaProteccionOutput implements Serializable {

  private Long id;

  private String nombre;

  private String descripcion;

  private TipoPropiedad tipoPropiedad;

  Integer mesesPrioridad;

  Boolean paisEspecifico;

  Boolean extensionInternacional;

  Boolean variosPaises;

  Boolean activo;
}
