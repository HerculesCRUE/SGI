package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

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
public class CspComInicioPresentacionGastoData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private LocalDate fecha;
  private List<Proyecto> proyectos;
  private String enlaceAplicacion;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonInclude(Include.NON_NULL)
  public static class Proyecto implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    private Collection<? extends I18nFieldValue> titulo;
    private Instant fechaInicio;
    private Instant fechaFin;
  }
}
