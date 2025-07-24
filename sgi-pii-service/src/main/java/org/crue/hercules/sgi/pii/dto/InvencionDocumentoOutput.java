package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.pii.model.InvencionDocumentoNombre;

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
public class InvencionDocumentoOutput {

  private Long id;
  private Instant fechaAnadido;
  private Collection<InvencionDocumentoNombre> nombre;
  private String documentoRef;
  private Long invencionId;

}
