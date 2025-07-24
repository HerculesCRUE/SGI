package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Duration;
import java.time.LocalTime;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class ActaObject {

  private Integer numero;
  private LocalTime inicio;
  private LocalTime fin;
  private Duration duracion;
  private String resumen;
  private String codigo;
  private ConvocatoriaReunionObject convocatoria;

  public ActaObject(ActaDto dto) {
    this.numero = dto.getNumero();
    this.inicio = LocalTime.of(dto.getHoraInicio(), dto.getMinutoInicio());
    this.fin = LocalTime.of(dto.getHoraFin(), dto.getMinutoFin());
    this.duracion = Duration.between(inicio, fin);
    this.resumen = I18nHelper.getFieldValue(dto.getResumen(), SgiReportContextHolder.getLanguage());
    this.codigo = numero + "/"
        + dto.getConvocatoriaReunion().getFechaEvaluacion().atZone(SgiReportContextHolder.getTimeZone().toZoneId())
            .getYear()
        + "/"
        + dto.getConvocatoriaReunion().getComite().getCodigo();
    this.convocatoria = new ConvocatoriaReunionObject(dto.getConvocatoriaReunion());
  }
}
