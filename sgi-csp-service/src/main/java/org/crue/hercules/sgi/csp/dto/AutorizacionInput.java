package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.Autorizacion;
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
public class AutorizacionInput implements Serializable {

  private List<I18nFieldValueDto> observaciones;
  private String responsableRef;
  private List<I18nFieldValueDto> tituloProyecto;
  private String entidadRef;
  private Integer horasDedicacion;

  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosResponsable;

  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosEntidad;

  private List<I18nFieldValueDto> datosConvocatoria;

  private Long convocatoriaId;
}
