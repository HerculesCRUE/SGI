package org.crue.hercules.sgi.eti.dto;

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
public class ApartadoTreeOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Long bloqueId;
  private Long padreId;
  private Integer orden;
  private List<ApartadoDefinicionOutput> definicion;
  private List<ApartadoTreeOutput> hijos;
}
