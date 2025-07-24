package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

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
public class CspComCalendarioFacturacionValidarIPData implements Serializable {
  private Collection<? extends I18nFieldValue> tituloProyecto;
  private Integer numPrevision;
  private List<String> codigosSge;
  private String nombreApellidosValidador;
  private Collection<? extends I18nFieldValue> motivoRechazo;
}
