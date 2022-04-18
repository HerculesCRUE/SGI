package org.crue.hercules.sgi.csp.dto.sgemp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private String id;
  private String nombre;
  private TipoIdentificadorOutput tipoIdentificador;
  private String numeroIdentificacion;
  private String razonSocial;
  private Boolean datosEconomicos;
  private String padreId;
}
