package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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
public class CspComCalendarioFacturacionNotificarData implements Serializable {
  private Collection<I18nFieldValueDto> tituloProyecto;
  private List<String> codigosSge;
  private Integer numPrevision;
  private List<String> entidadesFinanciadoras;
  private Collection<I18nFieldValueDto> tipoFacturacion;
  private String apellidosDestinatario;
  private boolean prorroga;
  private String enlaceAplicacion;
}
