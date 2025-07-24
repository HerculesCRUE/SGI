package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
public class ConvocatoriaFaseInput implements Serializable {

  @NotNull
  private Long convocatoriaId;
  @NotNull
  private Long tipoFaseId;
  @NotNull
  private Instant fechaInicio;
  @NotNull
  private Instant fechaFin;

  private List<I18nFieldValueDto> observaciones;
  @Valid
  private ConvocatoriaFaseAvisoInput aviso1;
  private ConvocatoriaFaseAvisoInput aviso2;
}
