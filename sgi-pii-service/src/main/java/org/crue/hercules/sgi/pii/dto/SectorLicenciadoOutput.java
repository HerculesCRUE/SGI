package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;

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
public class SectorLicenciadoOutput {
  private Long id;
  private Instant fechaInicioLicencia;
  private Instant fechaFinLicencia;
  private Long invencionId;
  private SectorAplicacion sectorAplicacion;
  private String contratoRef;
  private String paisRef;
  private Boolean exclusividad;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SectorAplicacion {
    private Long id;
    private String nombre;
    private String descripcion;
  }
}
