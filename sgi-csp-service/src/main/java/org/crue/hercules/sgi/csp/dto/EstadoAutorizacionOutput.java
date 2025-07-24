package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacionComentario;

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
public class EstadoAutorizacionOutput implements Serializable {
  private Long id;
  private Long autorizacionId;
  private Collection<EstadoAutorizacionComentario> comentario;
  private Instant fecha;
  private Estado estado;
}