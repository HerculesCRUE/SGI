package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
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
public class EstadoAutorizacionInput implements Serializable {

  @NotNull
  private Long autorizacionId;

  private List<I18nFieldValueDto> comentario;

  private Instant fecha;

  @NotNull
  private Estado estado;
}