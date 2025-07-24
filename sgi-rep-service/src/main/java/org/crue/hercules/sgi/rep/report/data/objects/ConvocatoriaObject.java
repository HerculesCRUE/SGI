package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.csp.ConvocatoriaDto;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class ConvocatoriaObject {

  private String codigo;
  private String titulo;

  public ConvocatoriaObject(ConvocatoriaDto dto) {
    this.codigo = dto.getCodigo();
    this.titulo = I18nHelper.getFieldValue(dto.getTitulo(), SgiReportContextHolder.getLanguage());
  }

}