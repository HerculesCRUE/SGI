package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.InvencionComentarios;
import org.crue.hercules.sgi.pii.model.InvencionDescripcion;
import org.crue.hercules.sgi.pii.model.InvencionTitulo;
import org.crue.hercules.sgi.pii.model.TipoProteccionNombre;

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
public class InvencionOutput {
  private Long id;
  private Collection<InvencionTitulo> titulo;
  private Instant fechaComunicacion;
  private Collection<InvencionDescripcion> descripcion;
  private Collection<InvencionComentarios> comentarios;
  private String proyectoRef;
  private TipoProteccion tipoProteccion;
  private Boolean activo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoProteccion {
    private Long id;
    private Collection<TipoProteccionNombre> nombre;
    private TipoProteccion padre;
    private TipoPropiedad tipoPropiedad;
  }
}
