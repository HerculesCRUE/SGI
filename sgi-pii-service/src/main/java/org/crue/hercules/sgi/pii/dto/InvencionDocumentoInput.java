package org.crue.hercules.sgi.pii.dto;

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
public class InvencionDocumentoInput implements Serializable {

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @NotEmpty
  private String documentoRef;

  @NotNull
  private Long invencionId;

}
