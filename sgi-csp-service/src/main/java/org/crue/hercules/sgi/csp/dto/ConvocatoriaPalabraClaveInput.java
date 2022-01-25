package org.crue.hercules.sgi.csp.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;

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
public class ConvocatoriaPalabraClaveInput {

  @NotNull
  private Long convocatoriaId;

  @NotNull
  @Size(max = ConvocatoriaPalabraClave.REF_LENGTH)
  private String palabraClaveRef;
}
