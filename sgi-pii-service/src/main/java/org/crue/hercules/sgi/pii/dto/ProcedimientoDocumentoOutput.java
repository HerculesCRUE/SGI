package org.crue.hercules.sgi.pii.dto;

import java.util.Collection;

import org.crue.hercules.sgi.pii.model.ProcedimientoDocumentoNombre;

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
  private Collection<ProcedimientoDocumentoNombre> nombre;
  private String documentoRef;
  private Long procedimientoId;

}
