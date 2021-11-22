package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloquesReportInput implements Serializable {
  // Si viene informado muestra los comentarios en cada apartado correspondiente
  private List<ComentarioDto> comentarios;

  // Si viene informado solo muestra los bloques que coincidan con los ids de la
  // lista
  private Set<Long> bloques;

  // Si viene informado solo muestra los apartados que coincidan con los ids de la
  // lista (tiene que contener toda la estructura jerarquica del apartado desde la
  // ráiz)
  private Set<Long> apartados;

  // Indica si se muestra el contenido de los apartados (solo mostraría el título
  // del apartado si es false)
  @NotNull
  private Boolean mostrarContenidoApartado;

  // Indica si se muestra la respuesta de los apartados
  @NotNull
  private Boolean mostrarRespuestas;

  @NotNull
  private Long idMemoria;

  @NotNull
  private Long idFormulario;
}
