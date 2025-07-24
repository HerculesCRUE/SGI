package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

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
public class GrupoEquipoInstrumentalOutput implements Serializable {

  private Long id;
  private String numRegistro;
  private List<I18nFieldValueDto> nombre;
  private List<I18nFieldValueDto> descripcion;
  private Long grupoId;

}
