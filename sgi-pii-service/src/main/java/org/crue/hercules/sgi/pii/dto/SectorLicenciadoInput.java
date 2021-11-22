package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.SectorLicenciado;
import org.springframework.format.annotation.DateTimeFormat;

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
public class SectorLicenciadoInput implements Serializable {
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaInicioLicencia;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaFinLicencia;

  @NotNull
  private Long invencionId;

  @NotNull
  private Long sectorAplicacionId;

  @NotNull
  @Size(max = SectorLicenciado.REF_LENGTH)
  private String contratoRef;

  @NotNull
  @Size(max = SectorLicenciado.REF_LENGTH)
  private String paisRef;

  @NotNull
  private Boolean exclusividad;
}
