package org.crue.hercules.sgi.pii.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;

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
public class InvencionAreaConocimientoInput {
  /** Invencion Id */
  @NotNull
  private Long invencionId;

  /** Identificador de un Area de Conocimiento */
  @NotEmpty
  @Size(max = InvencionAreaConocimiento.REF_LENGTH)
  private String areaConocimientoRef;
}
