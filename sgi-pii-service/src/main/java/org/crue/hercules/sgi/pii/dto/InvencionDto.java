package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
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
public class InvencionDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String titulo;
  private Long tipoProteccionId;

  private List<BigDecimal> participaciones;
  private List<SolicitudProteccionDto> solicitudesProteccion;
  private List<String> inventores;

  public InvencionDto(Long id, String titulo, Long tipoProteccionId) {
    this.id = id;
    this.titulo = titulo;
    this.tipoProteccionId = tipoProteccionId;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SolicitudProteccionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Instant fechaConcesion;
    private Long viaProteccionId;
  }

}
