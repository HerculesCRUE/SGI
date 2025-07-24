package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.eti.TipoEstadoMemoriaDto;
import org.crue.hercules.sgi.rep.enums.TipoEstadoMemoria;

import lombok.Getter;

@Getter
public class TipoEstadoMemoriaObject {
  private Long id;
  private String nombre;
  private Boolean activo;
  private TipoEstadoMemoria tipo;

  public TipoEstadoMemoriaObject(TipoEstadoMemoriaDto dto) {
    if (dto != null) {
      this.id = dto.getId();
      this.tipo = TipoEstadoMemoria.fromCode(id);
      this.nombre = tipo.toString();
      this.activo = dto.getActivo();
    }
  }

  @Override
  public String toString() {
    return tipo != null ? tipo.toString() : null;
  }
}
