package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class CertificadoAutorizacionInput implements Serializable {

  @NotNull
  private Long autorizacionId;

  @NotBlank
  private String documentoRef;

  @Size(max = 250)
  private String nombre;

  @NotNull
  private Boolean visible;

}
