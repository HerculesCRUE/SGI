package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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
  @NotEmpty
  private List<I18nFieldValueDto> nombreDocumento;
  private List<I18nFieldValueDto> incidencia;
}
