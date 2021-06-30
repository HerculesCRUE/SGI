package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.SectorAplicacion;

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
public class SectorAplicacionInput implements Serializable {
  @NotEmpty
  @Size(max = SectorAplicacion.NOMBRE_LENGTH)
  private String nombre;

  @NotEmpty
  @Size(max = SectorAplicacion.DESCRIPCION_LENGTH)
  private String descripcion;
}
