package org.crue.hercules.sgi.csp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgrupacionGastoConceptoOutput implements Serializable {
  private Long id;
  private Long agrupacionId;
  private ConceptoGasto conceptoGasto;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConceptoGasto implements Serializable {
    private Long id;
    private String nombre;
  }

}
