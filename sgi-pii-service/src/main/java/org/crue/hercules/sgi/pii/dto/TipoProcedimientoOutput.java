package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.pii.model.TipoProcedimientoDescripcion;
import org.crue.hercules.sgi.pii.model.TipoProcedimientoNombre;

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
public class TipoProcedimientoOutput implements Serializable {

  private Long id;

  private Collection<TipoProcedimientoNombre> nombre;

  private Collection<TipoProcedimientoDescripcion> descripcion;

  private Boolean activo;

}
