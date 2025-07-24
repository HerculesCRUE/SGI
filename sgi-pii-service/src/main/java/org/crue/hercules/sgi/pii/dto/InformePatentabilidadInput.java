package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.pii.model.InformePatentabilidad;
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
public class InformePatentabilidadInput {

  @NotNull
  private Long invencionId;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fecha;

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @NotEmpty
  @Size(max = InformePatentabilidad.REF_LENGTH)
  private String documentoRef;

  @NotNull
  private Long resultadoInformePatentabilidadId;

  @NotEmpty
  @Size(max = InformePatentabilidad.REF_LENGTH)
  private String entidadCreadoraRef;

  @Size(max = InformePatentabilidad.CONTACTO_LENGTH)
  private String contactoEntidadCreadora;

  @NotEmpty
  @Size(max = InformePatentabilidad.CONTACTO_LENGTH)
  private String contactoExaminador;

  private List<I18nFieldValueDto> comentarios;
}
