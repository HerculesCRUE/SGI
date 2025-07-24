package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Dictamen;
import org.crue.hercules.sgi.rep.enums.TipoEvaluacion;

import lombok.Getter;

@Getter
public class MemoriaEvaluadaObject {

  private String numReferencia;
  private Dictamen dictamen;
  private Integer version;
  private TipoEvaluacion tipoEvaluacion;
  private PersonaObject responsable;

  public MemoriaEvaluadaObject(MemoriaEvaluadaDto dto, PersonaDto responsableDto) {
    this.numReferencia = dto.getNumReferencia();
    this.dictamen = Dictamen.fromCode(dto.getDictamenId());
    this.version = dto.getVersion();
    this.tipoEvaluacion = TipoEvaluacion.fromCode(dto.getTipoEvaluacionId());
    if (responsableDto != null) {
      this.responsable = new PersonaObject(responsableDto);
    }
  }
}
