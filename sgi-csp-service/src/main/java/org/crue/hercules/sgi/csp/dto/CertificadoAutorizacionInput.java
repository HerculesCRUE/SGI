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
public class CertificadoAutorizacionInput implements Serializable {

  @NotNull
  private Long autorizacionId;

  @NotEmpty
  private List<I18nFieldValueDto> documentoRef;

  private List<I18nFieldValueDto> nombre;

  @NotNull
  private Boolean visible;

}
