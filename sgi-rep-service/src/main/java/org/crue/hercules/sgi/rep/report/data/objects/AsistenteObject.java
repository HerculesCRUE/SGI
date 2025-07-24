package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class AsistenteObject {
  private PersonaObject persona;
  private String motivo;

  public AsistenteObject(AsistentesDto asistente, PersonaDto persona) {
    this.motivo = I18nHelper.getFieldValue(asistente.getMotivo(), SgiReportContextHolder.getLanguage());
    if (persona != null) {
      this.persona = new PersonaObject(persona);
    }
  }
}