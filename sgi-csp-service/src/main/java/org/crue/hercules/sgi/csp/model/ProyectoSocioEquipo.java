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
@Table(name = "proyecto_socio_equipo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocioEquipo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_equipo_seq")
  @SequenceGenerator(name = "proyecto_socio_equipo_seq", sequenceName = "proyecto_socio_seq", allocationSize = 1)
  private Long id;

  /** ProyectoSocio Id */
  @Column(name = "proyecto_socio_id", nullable = false)
  @NotNull
  private Long proyectoSocioId;

  /** Rol proyecto. */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIOEQUIPO_ROLPROYECTO"))
  @NotNull
  private RolProyecto rolProyecto;

  /** Persona ref. */
  @Column(name = "persona_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String personaRef;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_socio_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIOEQUIPO_PROYECTOSOCIO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoSocio proyectoSocio = null;
}