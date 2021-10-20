package org.crue.hercules.sgi.rel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "relacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relacion extends BaseEntity {

  public static final int REF_LENGTH = 50;
  public static final int TIPO_ENTIDAD_LENGTH = 25;
  public static final int LONG_TEXT_LENGTH = 2000;

  public enum TipoEntidad {
    /** Proyecto */
    PROYECTO,
    /** Convocatoria */
    CONVOCATORIA,
    /** Invencion */
    INVENCION
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relacion_seq")
  @SequenceGenerator(name = "relacion_seq", sequenceName = "relacion_seq", allocationSize = 1)
  private Long id;

  /** Tipo de entidad origen */
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_entidad_origen", length = TIPO_ENTIDAD_LENGTH, nullable = false)
  private TipoEntidad tipoEntidadOrigen;

  /** Tipo de entidad destino */
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_entidad_destino", length = TIPO_ENTIDAD_LENGTH, nullable = false)
  private TipoEntidad tipoEntidadDestino;

  /** Entidad origen ref */
  @Column(name = "entidad_origen_ref", length = REF_LENGTH, nullable = false)
  private String entidadOrigenRef;

  /** Entidad destino ref */
  @Column(name = "entidad_destino_ref", length = REF_LENGTH, nullable = false)
  private String entidadDestinoRef;

  /** Observaciones */
  @Column(name = "observaciones", length = LONG_TEXT_LENGTH, nullable = true)
  private String observaciones;
}
