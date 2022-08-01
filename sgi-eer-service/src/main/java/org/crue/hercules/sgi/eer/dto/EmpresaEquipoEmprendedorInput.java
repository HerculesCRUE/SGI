package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;

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
public class EmpresaEquipoEmprendedorInput implements Serializable {
  private Long id;

  @Size(max = EmpresaEquipoEmprendedor.MIEMBRO_EQUIPO_REF_LENGTH)
  @NotBlank
  private String miembroEquipoRef;

  @NotNull
  private Long empresaId;

}
