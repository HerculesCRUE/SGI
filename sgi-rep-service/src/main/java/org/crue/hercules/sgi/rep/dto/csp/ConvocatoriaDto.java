package org.crue.hercules.sgi.rep.dto.csp;

import java.time.Instant;

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
public class ConvocatoriaDto extends BaseRestDto {

  /** Estados de la convocatoria */
  public enum Estado {
    /** Borrador */
    BORRADOR,
    /** Registrada */
    REGISTRADA;
  }

  /** Id */
  private Long id;

  /** Unidad Gestion */
  private String unidadGestionRef;

  /** Codigo */
  private String codigo;

  /** Fecha Publicación */
  private Instant fechaPublicacion;

  /** Fecha Provisional */
  private Instant fechaProvisional;

  /** Fecha Concesión */
  private Instant fechaConcesion;

  /** Titulo */
  private String titulo;

  /** Objeto */
  private String objeto;

  /** Observaciones */
  private String observaciones;

  /** Estado */
  private Estado estado;

  /** Duracion */
  private Integer duracion;

  /** Activo */
  private Boolean activo;

}