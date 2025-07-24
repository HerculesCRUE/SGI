package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.pii.model.SectorAplicacionDescripcion;
import org.crue.hercules.sgi.pii.model.SectorAplicacionNombre;

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
public class SectorAplicacionOutput implements Serializable {
  private Long id;
  private Collection<SectorAplicacionNombre> nombre;
  private Collection<SectorAplicacionDescripcion> descripcion;
  private Boolean activo;
}
