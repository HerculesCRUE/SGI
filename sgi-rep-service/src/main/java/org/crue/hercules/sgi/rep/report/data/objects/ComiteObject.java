package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteNombreDto;
import org.crue.hercules.sgi.rep.enums.Genero;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class ComiteObject {
  private Long id;
  private String codigo;
  private String nombre;
  private Genero genero;
  private Boolean activo;

  public ComiteObject(ComiteDto dto) {
    if (dto != null) {
      this.id = dto.getId();
      this.codigo = dto.getCodigo();
      ComiteNombreDto nombreDto = I18nHelper.getField(dto.getNombre(), SgiReportContextHolder.getLanguage())
          .orElse(null);
      if (nombreDto != null) {
        this.nombre = nombreDto.getValue();
        this.genero = nombreDto.getGenero() == ComiteNombreDto.Genero.M ? Genero.M : Genero.F;
      }
      this.activo = dto.getActivo();
    }
  }

  /** Se mantiene por compatibilidad con reports antiguos */
  public String getComite() {
    return codigo;
  }

  /** Se mantiene por compatibilidad con reports antiguos */
  public String getNombreInvestigacion() {
    return nombre;
  }

  /** Se mantiene por compatibilidad con reports antiguos */
  public Genero getGenero() {
    return genero;
  }
}
