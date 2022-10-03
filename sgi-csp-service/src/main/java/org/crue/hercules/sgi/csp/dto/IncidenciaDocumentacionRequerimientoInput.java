package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity;

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
public class IncidenciaDocumentacionRequerimientoInput implements Serializable {
  @NotNull
  private Long requerimientoJustificacionId;
  @NotBlank
  @Size(max = BaseEntity.DEFAULT_TEXT_LENGTH)
  private String nombreDocumento;
  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
  private String incidencia;
}
