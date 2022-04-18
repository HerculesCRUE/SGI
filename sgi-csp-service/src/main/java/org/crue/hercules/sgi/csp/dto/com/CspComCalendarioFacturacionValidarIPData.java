package org.crue.hercules.sgi.csp.dto.com;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CspComCalendarioFacturacionValidarIPData implements Serializable {
  private String tituloProyecto;
  private Integer numPrevision;
  private List<String> codigosSge;
  private String nombreApellidosValidador;
  private String motivoRechazo;
}
