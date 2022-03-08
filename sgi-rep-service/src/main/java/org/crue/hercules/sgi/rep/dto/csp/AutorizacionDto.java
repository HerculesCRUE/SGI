package org.crue.hercules.sgi.rep.dto.csp;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

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
public class AutorizacionDto extends BaseRestDto {

  /** Id */
  private Long id;

  /** Observaciones */
  private String observaciones;

  /** Responsable Ref */
  private String responsableRef;

  /** Solicitante Ref */
  private String solicitanteRef;

  /** Titulo Proyecto */
  private String tituloProyecto;

  /** Entidad Ref */
  private String entidadRef;

  /** Horas Dedicacion Ref */
  private Integer horasDedicacion;

  /** Datos Responsable */
  private String datosResponsable;

  /** Datos Entidad */
  private String datosEntidad;

  /** Datos Convocatoria */
  private String datosConvocatoria;

  /** Convocatoria */
  private Long convocatoriaId;

  /** Estado Autorizacion */
  private Long estadoId;

}