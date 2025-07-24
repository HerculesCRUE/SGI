package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
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
  private List<I18nFieldValueDto> resumen;
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