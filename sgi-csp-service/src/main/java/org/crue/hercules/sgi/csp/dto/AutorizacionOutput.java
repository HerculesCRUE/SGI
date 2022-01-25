package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

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
public class AutorizacionOutput implements Serializable {
  private Long id;
  private String observaciones;
  private String responsableRef;
  private String solicitanteRef;
  private String tituloProyecto;
  private String entidadRef;
  private Integer horasDedicacion;
  private String datosResponsable;
  private String datosEntidad;
  private String datosConvocatoria;
  private Long convocatoriaId;
  private Long estadoId;
}
