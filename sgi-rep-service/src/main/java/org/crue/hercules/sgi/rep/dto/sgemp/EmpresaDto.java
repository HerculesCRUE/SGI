package org.crue.hercules.sgi.rep.dto.sgemp;

import java.io.Serializable;

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
public class EmpresaDto implements Serializable {

  private String id;
  private String nombre;
  private TipoIdentificadorDto tipoIdentificador;
  private String numeroIdentificacion;
  private String razonSocial;
  private String datosEconomicos;
  private String padreId;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoIdentificadorDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

}