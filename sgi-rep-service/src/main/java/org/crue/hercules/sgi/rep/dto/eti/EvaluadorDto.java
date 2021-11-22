package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;
import java.time.Instant;

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
public class EvaluadorDto extends BaseRestDto {

  private CargoComiteDto cargoComite;
  private ComiteDto comite;
  private Instant fechaAlta;
  private Instant fechaBaja;
  private String resumen;
  private String personaRef;
  private Boolean activo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CargoComiteDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private Boolean activo;
  }

}