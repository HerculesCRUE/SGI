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
import javax.validation.constraints.Min;
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
@Table(name = "solicitud_proyecto_socio_equipo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoSocioEquipo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_equipo_socio_seq")
  @SequenceGenerator(name = "solicitud_proyecto_equipo_socio_seq", sequenceName = "solicitud_proyecto_equipo_socio_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyectoSocio Id */
  @Column(name = "solicitud_proyecto_socio_id", nullable = false)
  @NotNull
  private Long solicitudProyectoSocioId;

  /** Persona ref */
  @Column(name = "persona_ref", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String personaRef;

  /** Rol proyecto */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOSOCIOEQUIPO_ROLPROYECTO"))
  private RolProyecto rolProyecto;

  /** Mes Inicio */
  @Column(name = "mes_inicio", nullable = true)
  @Min(1)
  private Integer mesInicio;

  /** Mes Fin */
  @Column(name = "mes_fin", nullable = true)
  @Min(1)
  private Integer mesFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_socio_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOSOCIOEQUIPO_SOLICITUDPROYECTOSOCIO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyectoSocio solicitudProyectoSocio = null;
}
