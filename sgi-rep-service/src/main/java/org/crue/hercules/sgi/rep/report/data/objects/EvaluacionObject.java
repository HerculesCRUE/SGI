package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.enums.Dictamen;
import org.crue.hercules.sgi.rep.enums.TipoEvaluacion;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class EvaluacionObject {
  private Integer version;
  private TipoEvaluacion tipo;
  private Dictamen dictamen;
  private String comentario;
  private ConvocatoriaReunionObject convocatoria;
  private MemoriaObject memoria;

  public EvaluacionObject(EvaluacionDto dto) {
    this.version = dto.getVersion();
    this.tipo = TipoEvaluacion.fromCode(dto.getTipoEvaluacion().getId());
    if (dto.getDictamen() != null) {
      this.dictamen = Dictamen.fromCode(dto.getDictamen().getId());
    }
    this.comentario = I18nHelper.getFieldValue(dto.getComentario(), SgiReportContextHolder.getLanguage());
    this.convocatoria = new ConvocatoriaReunionObject(dto.getConvocatoriaReunion());
    this.memoria = new MemoriaObject(dto.getMemoria());
  }

}
