package org.crue.hercules.sgi.rep.dto.csp;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;

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
public class ProyectoEntidadFinanciadoraDto extends BaseRestDto {

  private Long proyectoId;
  private String entidadRef;
  private EmpresaDto empresa;
  private FuenteFinanciacionDto fuenteFinanciacion;
  private TipoFinanciacionDto tipoFinanciacion;
  private BigDecimal porcentajeFinanciacion;
  private BigDecimal importeFinanciacion;
  private Boolean ajena;
  private ProyectoDto proyecto;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoFinanciacionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private Boolean activo;
  }
}