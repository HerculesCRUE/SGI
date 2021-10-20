package org.crue.hercules.sgi.rel.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.rel.model.Relacion;
import org.crue.hercules.sgi.rel.model.Relacion.TipoEntidad;

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
public class RelacionInput {

  @NotNull
  private TipoEntidad tipoEntidadOrigen;

  @NotNull
  private TipoEntidad tipoEntidadDestino;

  @NotNull
  @Size(max = Relacion.REF_LENGTH)
  private String entidadOrigenRef;

  @NotNull
  @Size(max = Relacion.REF_LENGTH)
  private String entidadDestinoRef;

  @Size(max = Relacion.LONG_TEXT_LENGTH)
  private String observaciones;
}
