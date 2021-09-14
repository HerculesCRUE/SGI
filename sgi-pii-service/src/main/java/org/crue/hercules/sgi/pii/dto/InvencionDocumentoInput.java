package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.InvencionDocumento;

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
public class InvencionDocumentoInput implements Serializable {

  @NotEmpty
  @Size(max = InvencionDocumento.NOMBRE_LENGTH)
  private String nombre;

  @NotEmpty
  private String documentoRef;

  @NotNull
  private Long invencionId;

}
