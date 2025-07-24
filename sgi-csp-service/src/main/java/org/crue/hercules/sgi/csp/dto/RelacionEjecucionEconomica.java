package org.crue.hercules.sgi.csp.dto;

import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelacionEjecucionEconomica {

  public enum TipoEntidad {
    /** Grupo */
    GRUPO,
    /** Proyecto */
    PROYECTO
  }

  private Long id;
  private Collection<I18nFieldValueDto> nombre;
  private String codigoExterno;
  private String codigoInterno;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String proyectoSgeRef;
  private TipoEntidad tipoEntidad;
  private Instant fechaFinDefinitiva;

}
