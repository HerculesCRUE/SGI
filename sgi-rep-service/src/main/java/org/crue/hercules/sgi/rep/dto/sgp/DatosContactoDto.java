package org.crue.hercules.sgi.rep.dto.sgp;

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
public class DatosContactoDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private PaisDto paisContacto;
  private ComunidadAutonomaDto comAutonomaContacto;
  private ProvinciaDto provinciaContacto;
  private String ciudadContacto;
  private String codigoPostalContacto;
  private String direccionContacto;
  private List<EmailDto> emails;
  private List<String> telefonos;
  private List<String> moviles;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PaisDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ComunidadAutonomaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private Long paisId;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProvinciaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private Long comunidadAutonomaId;
  }
}