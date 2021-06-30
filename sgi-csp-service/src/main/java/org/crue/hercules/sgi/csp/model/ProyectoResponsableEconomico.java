package org.crue.hercules.sgi.csp.model;

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

import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico.OnActualizar;
import org.crue.hercules.sgi.csp.validation.OrderFechasProyectoResponsableEconomico;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_responsable_economico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@OrderFechasProyectoResponsableEconomico(groups = { OnActualizar.class })
public class ProyectoResponsableEconomico extends BaseEntity {
  public static final int PERSONA_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_responsable_economico_seq")
  @SequenceGenerator(name = "proyecto_responsable_economico_seq", sequenceName = "proyecto_responsable_economico_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  private Long proyectoId;

  /** Persona ref. */
  @Column(name = "persona_ref", length = PERSONA_REF_LENGTH, nullable = false)
  private String personaRef;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTORESPONSABLEECONOMICO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

}