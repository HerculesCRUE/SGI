package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
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
public class GrupoEquipoInstrumentalInput implements Serializable {
  private Long id;

  @Size(max = GrupoEquipoInstrumental.NUM_REGISTRO_LENGTH)
  private String numRegistro;

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  private List<I18nFieldValueDto> descripcion;

  @NotNull
  private Long grupoId;

}
