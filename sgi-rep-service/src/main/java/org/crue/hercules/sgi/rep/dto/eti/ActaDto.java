package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;
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
public class ActaDto extends BaseRestDto {

  private ConvocatoriaReunionDto convocatoriaReunion;
  private Integer horaInicio;
  private Integer minutoInicio;
  private Integer horaFin;
  private Integer minutoFin;
  private List<I18nFieldValueDto> resumen;
  private Integer numero;
  private TipoEstadoActaDto estadoActual;
  private Boolean inactiva;
  private Boolean activo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoEstadoActaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private Boolean activo;
  }
}