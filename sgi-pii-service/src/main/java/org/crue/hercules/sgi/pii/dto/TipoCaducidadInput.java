package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

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
public class TipoCaducidadInput implements Serializable {

  @NotEmpty
  private List<I18nFieldValueDto> descripcion;

}
