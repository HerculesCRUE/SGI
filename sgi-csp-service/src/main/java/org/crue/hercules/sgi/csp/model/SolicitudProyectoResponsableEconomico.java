package org.crue.hercules.sgi.csp.model;

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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico.OnActualizar;
import org.crue.hercules.sgi.csp.validation.OrderMesesSolicitudProyectoResponsableEconomico;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_proyecto_responsable_economico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@OrderMesesSolicitudProyectoResponsableEconomico(groups = { OnActualizar.class })
public class SolicitudProyectoResponsableEconomico extends BaseEntity {
  public static final int PERSONA_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_responsable_economico_seq")
  @SequenceGenerator(name = "solicitud_proyecto_responsable_economico_seq", sequenceName = "solicitud_proyecto_responsable_economico_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyecto Id */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  @NotNull
  private Long solicitudProyectoId;

  /** Persona ref. */
  @Column(name = "persona_ref", length = PERSONA_REF_LENGTH, nullable = false)
  @Size(max = PERSONA_REF_LENGTH)
  @NotEmpty
  private String personaRef;

  /** Mes de inicio */
  @Column(name = "mes_inicio", nullable = true)
  private Integer mesInicio;

  /** Mes de fin */
  @Column(name = "mes_fin", nullable = true)
  private Integer mesFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTORESPONSABLEECONOMICO_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyecto solicitudProyecto = null;

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

}