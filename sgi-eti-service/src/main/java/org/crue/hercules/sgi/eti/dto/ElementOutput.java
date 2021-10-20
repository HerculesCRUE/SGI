package org.crue.hercules.sgi.eti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementOutput {
  private String nombre;
  private String tipo;
  private String content;
}
