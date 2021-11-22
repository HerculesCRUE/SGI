package org.crue.hercules.sgi.pii.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Procedimiento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Procedimiento extends BaseEntity {
  public static final String TABLE_NAME = "procedimiento";
  public static final String SEQ_SUFFIX = "_seq";
  public static final int LONG_TEXT_LENGTH = 500;
  public static final int COMENTARIOS_MAX_LENGTH = 2000;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_NAME + SEQ_SUFFIX)
  @SequenceGenerator(name = TABLE_NAME + SEQ_SUFFIX, sequenceName = TABLE_NAME + SEQ_SUFFIX, allocationSize = 1)
  private Long id;

  /** Fecha */
  @Column(name = "fecha", nullable = false)
  private Instant fecha;

  /** Tipo de Procedimiento. */
  @ManyToOne
  @JoinColumn(name = "tipo_procedimiento_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROCEDIMIENTO_TIPOPROCEDIMIENTO"))
  @ActivableIsActivo(entityClass = TipoProcedimiento.class, groups = { OnCrear.class, OnActualizar.class })
  private TipoProcedimiento tipoProcedimiento;

  /** SolicitudProteccion Id */
  @Column(name = "solicitud_proteccion_id", nullable = false)
  private Long solicitudProteccionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proteccion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROCEDIMIENTO_SOLICITUDPROTECCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProteccion solicitudProteccion = null;

  /** Acciones a Tomar */
  @Column(name = "accion_a_tomar", length = LONG_TEXT_LENGTH, nullable = true)
  private String accionATomar;

  /** Fecha límite del procedimiento asociado a la solicitud */
  @Column(name = "fecha_limite_accion", nullable = true)
  private Instant fechaLimiteAccion;

  /** Generar Aviso */
  @Column(name = "generar_aviso", nullable = true)
  private Boolean generarAviso;

  @Column(name = "comentarios", length = COMENTARIOS_MAX_LENGTH, nullable = true)
  private String comentarios;

  /**
   * Interfaz para marcar validaciones en la creacion de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

}
