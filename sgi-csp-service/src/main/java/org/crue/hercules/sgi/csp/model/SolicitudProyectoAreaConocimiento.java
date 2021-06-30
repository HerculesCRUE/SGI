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
import javax.persistence.UniqueConstraint;
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
@Table(name = "solicitud_proyecto_area", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "solicitud_proyecto_id",
        "area_conocimiento_ref" }, name = "UK_SOLICITUDPROYECTOAREACONOCIMIENTO_SOLICITUDPROYECTO_AREA") })

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoAreaConocimiento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_area_conocimiento_seq")
  @SequenceGenerator(name = "solicitud_proyecto_area_conocimiento_seq", sequenceName = "solicitud_proyecto_area_conocimiento_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyecto Id */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  @NotNull
  private Long solicitudProyectoId;

  /** Area Conocimiento */
  @Column(name = "area_conocimiento_ref", nullable = true)
  private String areaConocimientoRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_AREASCONOCIMIENTO_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyecto solicitudProyecto = null;
}
