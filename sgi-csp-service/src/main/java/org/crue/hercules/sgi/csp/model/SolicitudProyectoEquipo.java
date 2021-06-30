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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_proyecto_equipo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoEquipo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_equipo_seq")
  @SequenceGenerator(name = "solicitud_proyecto_equipo_seq", sequenceName = "solicitud_proyecto_equipo_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyecto Id */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  @NotNull
  private Long solicitudProyectoId;

  /** Persona ref */
  @Column(name = "persona_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String personaRef;

  /** Rol Proyecto */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOEQUIPO_ROLPROYECTO"))
  @NotNull
  private RolProyecto rolProyecto;

  /** Mes de inicio */
  @Column(name = "mes_inicio", nullable = true)
  private Integer mesInicio;

  /** Mes de fin */
  @Column(name = "mes_fin", nullable = true)
  private Integer mesFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOEQUIPO_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyecto solicitudProyecto = null;

  /**
   * Interfaz para marcar validaciones en la creación de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo RolProyecto
   * de la entidad.
   */
  public interface OnActualizarRolProyectoSolicitudProyectoEquipo {

  }

}
