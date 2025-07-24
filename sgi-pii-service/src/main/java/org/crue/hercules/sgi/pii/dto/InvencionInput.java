package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.pii.model.Invencion;
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
public class InvencionInput implements Serializable {
  @NotEmpty
  private List<I18nFieldValueDto> titulo;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaComunicacion;

  @NotEmpty
  private List<I18nFieldValueDto> descripcion;

  private List<I18nFieldValueDto> comentarios;

  private String proyectoRef;

  @NotNull
  private Long tipoProteccionId;
}
