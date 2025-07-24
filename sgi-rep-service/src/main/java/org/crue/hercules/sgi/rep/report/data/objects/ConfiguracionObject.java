package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.eti.ConfiguracionDto;

import lombok.Getter;

@Getter
public class ConfiguracionObject {
  private Integer diasArchivadaInactivo;
  private Integer mesesArchivadaPendienteCorrecciones;

  public ConfiguracionObject(ConfiguracionDto dto) {
    this.diasArchivadaInactivo = dto.getDiasArchivadaInactivo();
    this.mesesArchivadaPendienteCorrecciones = dto.getMesesArchivadaPendienteCorrecciones();
  }

}
