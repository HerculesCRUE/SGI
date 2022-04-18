package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.csp.model.TipoHito;

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
public class SolicitudHitoOutput implements Serializable {
  private Long id;
  private Long solicitudId;
  private TipoHito tipoHito;
  private Instant fecha;
  private String comentario;
  private SolicitudHitoAvisoOutput aviso;
}
