package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoComentario;
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
public class ConvocatoriaHitoOutput implements Serializable {
  private Long id;
  private Long convocatoriaId;
  private TipoHito tipoHito;
  private Instant fecha;
  private Collection<ConvocatoriaHitoComentario> comentario;
  private ConvocatoriaHitoAvisoOutput aviso;
}
