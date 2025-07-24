package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;

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
public class ProcedimientoInput {

  @NotNull
  private Instant fecha;

  @NotNull
  private Long tipoProcedimientoId;

  @NotNull
  private Long solicitudProteccionId;

  private List<I18nFieldValueDto> accionATomar;

  private Instant fechaLimiteAccion;

  private Boolean generarAviso;

  private List<I18nFieldValueDto> comentarios;

}
