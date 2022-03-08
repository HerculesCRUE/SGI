package org.crue.hercules.sgi.prc.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = EstadoProduccionCientifica.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoProduccionCientifica extends BaseEntity {

  protected static final String TABLE_NAME = "estado_produccion_cientifica";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Tipo estado producción */
  public enum TipoEstadoProduccion {
    /** Pendiente */
    PENDIENTE,
    /** Validado */
    VALIDADO,
    /** Validado parcíalmente */
    VALIDADO_PARCIALMENTE,
    /** Rechazado */
    RECHAZADO;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** comentario */
  @Column(name = "comentario", length = COMENTARIO_LENGTH, nullable = true)
  private String comentario;

  /** Fecha */
  @Column(name = "fecha", nullable = false)
  private Instant fecha;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  /** TipoEstadoProduccion */
  @Column(name = "estado", length = TIPO_ESTADO_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoEstadoProduccion estado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOPRODUCCIONCIENTIFICA_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

}
