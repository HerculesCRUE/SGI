package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.Autorizacion;

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

  @Size(max = Autorizacion.MAX_LENGTH)
  private String observaciones;

  private String responsableRef;
  private String tituloProyecto;
  private String entidadRef;
  private Integer horasDedicacion;

  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosResponsable;

  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosEntidad;

  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosConvocatoria;

  private Long convocatoriaId;
}
