package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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