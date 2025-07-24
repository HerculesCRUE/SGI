package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;

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
public class TipoProteccionInput implements Serializable {

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @NotEmpty
  private List<I18nFieldValueDto> descripcion;

  @NotNull
  private TipoPropiedad tipoPropiedad;

  private Long padreId;

}
