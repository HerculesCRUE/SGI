package org.crue.hercules.sgi.eer.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.BaseEntity;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @NotBlank
  @Size(max = BaseEntity.REF_LENGTH)
  private String documentoRef;

  private List<I18nFieldValueDto> comentarios;

  @NotNull
  private Long empresaId;

  private Long tipoDocumentoId;
}
