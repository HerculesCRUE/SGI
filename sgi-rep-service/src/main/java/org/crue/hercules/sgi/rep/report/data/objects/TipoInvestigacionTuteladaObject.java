package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.eti.TipoInvestigacionTuteladaDto;
import org.crue.hercules.sgi.rep.enums.TipoInvestigacionTutelada;

import lombok.Getter;

@Getter
public class TipoInvestigacionTuteladaObject {

  private Long id;
  private String nombre;
  private Boolean activo;
  private TipoInvestigacionTutelada tipo;

  public TipoInvestigacionTuteladaObject(TipoInvestigacionTuteladaDto dto) {
    if (dto != null) {
      this.id = dto.getId();
      this.tipo = TipoInvestigacionTutelada.fromCode(id);
      this.nombre = tipo.toString();
      this.activo = dto.getActivo();
    }
  }

  @Override
  public String toString() {
    return tipo != null ? tipo.toString() : null;
  }
}
