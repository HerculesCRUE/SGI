package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CspComInicioPresentacionSeguimientoCientificoData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private String enlaceAplicacion;
  private LocalDate fecha;
  private List<Proyecto> proyectos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonInclude(Include.NON_NULL)
  public static class Proyecto implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    private Collection<I18nFieldValueDto> titulo;
    private Instant fechaInicio;
    private Instant fechaFin;
  }
}
