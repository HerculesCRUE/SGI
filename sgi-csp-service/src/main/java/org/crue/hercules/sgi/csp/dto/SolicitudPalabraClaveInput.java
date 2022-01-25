package org.crue.hercules.sgi.csp.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave;

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
public class SolicitudPalabraClaveInput {

  @NotNull
  private Long solicitudId;

  @NotNull
  @Size(max = SolicitudPalabraClave.REF_LENGTH)
  private String palabraClaveRef;
}
