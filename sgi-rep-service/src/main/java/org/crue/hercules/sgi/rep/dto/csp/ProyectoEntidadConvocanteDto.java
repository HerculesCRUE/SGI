package org.crue.hercules.sgi.rep.dto.csp;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;

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
public class ProyectoEntidadConvocanteDto extends BaseRestDto {

  private String entidadRef;
  private EmpresaDto empresa;
  private Long proyectoId;
  private ProgramaDto programaConvocatoria;
  private ProgramaDto programa;
  private ProyectoDto proyecto;
}