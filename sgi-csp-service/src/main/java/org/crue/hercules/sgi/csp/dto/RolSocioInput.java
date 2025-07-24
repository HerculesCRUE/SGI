package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import org.crue.hercules.sgi.csp.model.RolSocio;

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
public class RolSocioInput implements Serializable {

  @NotEmpty
  @Size(max = RolSocio.NOMBRE_LENGTH)
  private List<I18nFieldValueDto> nombre;

  @NotEmpty
  @Size(max = RolSocio.ABREVIATURA_LENGTH)
  private List<I18nFieldValueDto> abreviatura;

  @NotNull
  private Boolean coordinador;

  @Size(max = RolSocio.DESCRIPCION_LENGTH)
  private List<I18nFieldValueDto> descripcion;
}
