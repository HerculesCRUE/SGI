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
public class InformePatentabilidadOutput {
  private Long id;
  private Long invencionId;
  private Instant fecha;
  private String nombre;
  private String documentoRef;
  private ResultadoInformePatentabilidad resultadoInformePatentabilidad;
  private String entidadCreadoraRef;
  private String contactoEntidadCreadora;
  private String contactoExaminador;
  private String comentarios;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResultadoInformePatentabilidad {
    private Long id;
    private String nombre;
    private String descripcion;
  }
}
