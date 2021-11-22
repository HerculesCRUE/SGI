package org.crue.hercules.sgi.rep.dto.csp;

import java.io.Serializable;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FuenteFinanciacionDto extends BaseRestDto {

  private String nombre;
  private String descripcion;
  private Boolean fondoEstructural;
  private TipoAmbitoGeograficoDto tipoAmbitoGeografico;
  private TipoOrigenFuenteFinanciacionDto tipoOrigenFuenteFinanciacion;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoOrigenFuenteFinanciacionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
  }
}