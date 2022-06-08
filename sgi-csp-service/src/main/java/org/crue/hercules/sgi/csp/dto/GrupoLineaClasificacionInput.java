package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;

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
public class GrupoLineaClasificacionInput implements Serializable {

  @NotNull
  private Long grupoLineaInvestigacionId;

  @Size(max = GrupoLineaClasificacion.CLASIFICACION_REF_LENGTH)
  @NotBlank
  private String clasificacionRef;

}
