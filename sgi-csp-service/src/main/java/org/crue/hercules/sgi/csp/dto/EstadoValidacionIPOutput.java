package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

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
public class EstadoValidacionIPOutput implements Serializable {

  public enum TipoEstadoValidacion {
    PENDIENTE, NOTIFICADA, VALIDADA, RECHAZADA
  }

  private Long id;
  private TipoEstadoValidacion estado;
  private I18nFieldValue[] comentario;
  private Instant fecha;
  private Long proyectoFacturacionId;
}
