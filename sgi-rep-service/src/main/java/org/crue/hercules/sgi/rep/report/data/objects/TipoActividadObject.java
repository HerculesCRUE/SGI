package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.eti.TipoActividadDto;
import org.crue.hercules.sgi.rep.enums.TipoActividad;

import lombok.Getter;

@Getter
public class TipoActividadObject {

  private Long id;
  private String nombre;
  private Boolean activo;
  private TipoActividad tipo;

  public TipoActividadObject(TipoActividadDto dto) {
    if (dto != null) {
      this.id = dto.getId();
      this.tipo = TipoActividad.fromCode(id);
      this.nombre = tipo.toString();
      this.activo = dto.getActivo();
    }
  }

  @Override
  public String toString() {
    return tipo != null ? tipo.toString() : null;
  }
}
