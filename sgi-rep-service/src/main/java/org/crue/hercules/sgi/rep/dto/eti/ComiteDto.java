package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ComiteDto extends BaseRestDto {

  private String codigo;
  private List<ComiteNombreDto> nombre;
  private Long formularioMemoriaId;
  private Long formularioSeguimientoAnualId;
  private Long formularioSeguimientoFinalId;
  private Long formularioRetrospectivaId;
  private Boolean requiereRetrospectiva;
  private String prefijoReferencia;
  private Boolean permitirRatificacion;
  private Boolean tareaNombreLibre;
  private Boolean tareaExperienciaLibre;
  private Boolean tareaExperienciaDetalle;
  private Boolean memoriaTituloLIibre;
  private Boolean activo;

}