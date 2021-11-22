package org.crue.hercules.sgi.pii.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.ProcedimientoDocumento;

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
public class ProcedimientoDocumentoInput {

  @Size(max = ProcedimientoDocumento.NOMBRE_LENGTH)
  @NotEmpty
  private String nombre;

  @Size(max = ProcedimientoDocumento.FICHERO_LENGTH)
  @NotEmpty
  private String documentoRef;

  @NotNull
  private Long procedimientoId;

}
