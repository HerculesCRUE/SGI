package org.crue.hercules.sgi.pii.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
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

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @Size(max = ProcedimientoDocumento.FICHERO_LENGTH)
  @NotEmpty
  private String documentoRef;

  @NotNull
  private Long procedimientoId;

}
