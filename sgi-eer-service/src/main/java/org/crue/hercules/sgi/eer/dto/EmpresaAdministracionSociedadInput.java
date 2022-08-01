package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;

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
public class EmpresaAdministracionSociedadInput implements Serializable {
  private Long id;

  @Size(max = EmpresaAdministracionSociedad.REF_LENGTH)
  @NotNull
  private String miembroEquipoAdministracionRef;

  @NotNull
  private Instant fechaInicio;

  private Instant fechaFin;

  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoAdministracion tipoAdministracion;

  @NotNull
  private Long empresaId;

}
