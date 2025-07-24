package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoTipo.Tipo;
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
public class GrupoInput implements Serializable {

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @NotNull
  private Instant fechaInicio;

  private Instant fechaFin;

  @Size(max = Grupo.PROYECTO_SGE_REF_LENGTH)
  private String proyectoSgeRef;

  private Long solicitudId;

  @Size(max = Grupo.CODIGO_LENGTH)
  private String codigo;

  private Tipo tipo;

  private Boolean especialInvestigacion;

  private List<I18nFieldValueDto> resumen;

  private String departamentoOrigenRef;

}
