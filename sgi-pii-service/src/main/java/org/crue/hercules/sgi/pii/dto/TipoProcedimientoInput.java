package org.crue.hercules.sgi.pii.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.TipoProcedimiento;

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
public class TipoProcedimientoInput {

  @NotEmpty
  @Size(max = TipoProcedimiento.NOMBRE_LENGTH)
  private String nombre;

  @NotEmpty
  @Size(max = TipoProcedimiento.DESCRIPCION_LENGTH)
  private String descripcion;
}
