package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartadoOutput {
  private Long id;
  private String nombre;
  private String titulo;
  private Integer orden;

  private List<ElementOutput> elementos;

  private List<ApartadoOutput> apartadosHijos;

  @JsonIgnore
  private String esquema;

  @JsonIgnore
  private RespuestaDto respuesta;

  // Indica si se muestra el contenido de los apartados (solo mostraría el título
  // del apartado si es false)
  @JsonIgnore
  private Boolean mostrarContenidoApartado;

  @JsonIgnore
  private List<ComentarioDto> comentarios;

  @JsonIgnore
  private Boolean modificado;

  @JsonIgnore
  private Integer numeroComentariosGestor;

}
