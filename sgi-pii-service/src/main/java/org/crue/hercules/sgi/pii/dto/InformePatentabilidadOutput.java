package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.pii.model.InformePatentabilidadComentarios;
import org.crue.hercules.sgi.pii.model.InformePatentabilidadNombre;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidadDescripcion;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidadNombre;

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
  private Collection<InformePatentabilidadNombre> nombre;
  private String documentoRef;
  private ResultadoInformePatentabilidad resultadoInformePatentabilidad;
  private String entidadCreadoraRef;
  private String contactoEntidadCreadora;
  private String contactoExaminador;
  private Collection<InformePatentabilidadComentarios> comentarios;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ResultadoInformePatentabilidad {
    private Long id;
    private Collection<ResultadoInformePatentabilidadNombre> nombre;
    private Collection<ResultadoInformePatentabilidadDescripcion> descripcion;
  }
}
