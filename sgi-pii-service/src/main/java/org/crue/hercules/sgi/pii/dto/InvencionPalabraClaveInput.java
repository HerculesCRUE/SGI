package org.crue.hercules.sgi.pii.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;

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
public class InvencionPalabraClaveInput {

  @NotNull
  private Long invencionId;

  @NotNull
  @Size(max = InvencionPalabraClave.REF_LENGTH)
  private String palabraClaveRef;
}
