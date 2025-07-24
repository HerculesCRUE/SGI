package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Duration;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class AutorizacionObject {

  private String tituloProyecto;
  private Duration horasDedicacion;
  private String datosConvocatoria;
  private String datosEntidad;
  private String datosResponsable;

  public AutorizacionObject(AutorizacionDto dto) {
    this.tituloProyecto = I18nHelper.getFieldValue(dto.getTituloProyecto(), SgiReportContextHolder.getLanguage());
    if (dto.getHorasDedicacion() != null) {
      this.horasDedicacion = Duration.ofHours(dto.getHorasDedicacion());
    }
    this.datosConvocatoria = I18nHelper.getFieldValue(dto.getDatosConvocatoria(), SgiReportContextHolder.getLanguage());
    this.datosEntidad = dto.getDatosEntidad();
    this.datosResponsable = dto.getDatosResponsable();
  }
}
