package org.crue.hercules.sgi.eti.dto;

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

  @JsonIgnore
  private String esquema;

  @JsonIgnore
  private RespuestaOutput respuesta;

  private List<ElementOutput> elementos;

  private List<ApartadoOutput> apartadosHijos;
}
