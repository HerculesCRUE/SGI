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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_proyecto_entidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoEntidad extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_entidad_seq")
  @SequenceGenerator(name = "solicitud_proyecto_entidad_seq", sequenceName = "solicitud_proyecto_entidad_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyecto Id */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  @NotNull
  private Long solicitudProyectoId;

  /** Entidad financiadora ajena */
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_entidad_financiadora_ajena_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDAD_ENTIDADFINANCIADORAAJENA"))
  private SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena;

  /** Entidad financiadora convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_entidad_financiadora_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDAD_CONVOCATORIAENTIDADFINANCIADORA"))
  private ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora;

  /** Entidad gestora convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_entidad_gestora_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDAD_CONVOCATORIAENTIDADGESTORA"))
  private ConvocatoriaEntidadGestora convocatoriaEntidadGestora;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDAD_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyecto solicitudProyecto = null;
}
