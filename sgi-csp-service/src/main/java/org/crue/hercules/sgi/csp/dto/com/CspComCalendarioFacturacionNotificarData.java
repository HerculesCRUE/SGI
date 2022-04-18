package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.util.List;

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
  private String tituloProyecto;
  private List<String> codigosSge;
  private Integer numPrevision;
  private List<String> entidadesFinanciadoras;
  private String tipoFacturacion;
  private String apellidosDestinatario;
  private boolean prorroga;
}
