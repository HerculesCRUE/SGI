package org.crue.hercules.sgi.rep.dto.csp;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
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
  private List<I18nFieldValueDto> titulo;

  /** Objeto */
  private List<I18nFieldValueDto> objeto;

  /** Observaciones */
  private List<I18nFieldValueDto> observaciones;

  /** Estado */
  private Estado estado;

  /** Duracion */
  private Integer duracion;

  /** Activo */
  private Boolean activo;

}