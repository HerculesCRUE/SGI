package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.eti.EstadoRetrospectivaDto;
import org.crue.hercules.sgi.rep.enums.EstadoRetrospectiva;

import lombok.Getter;

@Getter
public class EstadoRetrospectivaObject {
  private Long id;
  private String nombre;
  private Boolean activo;
  private EstadoRetrospectiva estado;

  public EstadoRetrospectivaObject(EstadoRetrospectivaDto dto) {
    if (dto != null) {
      this.id = dto.getId();
      this.estado = EstadoRetrospectiva.fromCode(id);
      this.nombre = estado.toString();
      this.activo = dto.getActivo();
    }
  }

  @Override
  public String toString() {
    return estado != null ? estado.toString() : null;
  }
}
