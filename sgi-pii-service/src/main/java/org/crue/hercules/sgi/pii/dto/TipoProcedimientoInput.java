package org.crue.hercules.sgi.pii.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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
  private List<I18nFieldValueDto> nombre;

  @NotEmpty
  private List<I18nFieldValueDto> descripcion;
}
