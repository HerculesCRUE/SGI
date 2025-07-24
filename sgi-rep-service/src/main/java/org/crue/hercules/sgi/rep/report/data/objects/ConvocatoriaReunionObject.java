package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.TipoConvocatoriaReunionI18n;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class ConvocatoriaReunionObject {

  private Instant fechaEvaluacion;
  private ComiteObject comite;
  private String lugar;
  private Boolean videoConferencia;
  private String tipo;
  private String ordenDia;
  private Long numeroActa;
  private Integer anio;

  public ConvocatoriaReunionObject(ConvocatoriaReunionDto dto) {
    this.fechaEvaluacion = dto.getFechaEvaluacion();
    this.comite = new ComiteObject(dto.getComite());
    this.lugar = I18nHelper.getFieldValue(dto.getLugar(), SgiReportContextHolder.getLanguage());
    this.videoConferencia = dto.getVideoconferencia();
    if (dto.getTipoConvocatoriaReunion() != null) {
      this.tipo = TipoConvocatoriaReunionI18n.getI18nMessageFromEnumAndLocale(dto.getTipoConvocatoriaReunion().getId(),
          SgiReportContextHolder.getLanguage().getLocale());
    }
    this.ordenDia = I18nHelper.getFieldValue(dto.getOrdenDia(), SgiReportContextHolder.getLanguage());
    this.numeroActa = dto.getNumeroActa();
    this.anio = dto.getAnio();
  }

  public String getCodigoActa() {
    return numeroActa + "/" + anio;
  }
}
