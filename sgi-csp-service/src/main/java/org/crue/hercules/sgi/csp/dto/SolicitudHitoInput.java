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
public class SolicitudHitoInput implements Serializable {

  @NotNull
  private Long solicitudId;
  @NotNull
  private Long tipoHitoId;
  @NotNull
  private Instant fecha;

  private List<I18nFieldValueDto> comentario;
  @Valid
  private SolicitudHitoAvisoInput aviso;
}
