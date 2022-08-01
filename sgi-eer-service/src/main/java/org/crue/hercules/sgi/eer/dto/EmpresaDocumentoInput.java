package org.crue.hercules.sgi.eer.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.BaseEntity;

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
public class EmpresaDocumentoInput {

  @NotBlank
  @Size(max = BaseEntity.SHORT_TEXT_LENGTH)
  private String nombre;

  @NotBlank
  @Size(max = BaseEntity.REF_LENGTH)
  private String documentoRef;

  @Size(max = BaseEntity.LONG_TEXT_LENGTH)
  private String comentarios;

  @NotNull
  private Long empresaId;

  private Long tipoDocumentoId;
}
