package org.crue.hercules.sgi.pii.dto;

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
public class ProcedimientoDocumentoOutput {

  private Long id;
  private String nombre;
  private String documentoRef;
  private Long procedimientoId;

}
