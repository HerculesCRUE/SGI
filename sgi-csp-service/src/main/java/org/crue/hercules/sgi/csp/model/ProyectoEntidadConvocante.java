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
import javax.validation.constraints.NotEmpty;
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
@Table(name = "proyecto_entidad_convocante", uniqueConstraints = { @UniqueConstraint(columnNames = { "proyecto_id",
    "entidad_ref" }, name = "UK_PROYECTOENTIDADCONVOCANTE_PROYECTO_ENTIDAD") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoEntidadConvocante extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_entidad_convocante_seq")
  @SequenceGenerator(name = "proyecto_entidad_convocante_seq", sequenceName = "proyecto_entidad_convocante_seq", allocationSize = 1)
  private Long id;

  /** Entidad Convocante */
  @Column(name = "entidad_ref", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String entidadRef;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Programa heredado de la Convocatoria */
  @ManyToOne
  @JoinColumn(name = "programa_convocatoria_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADCONVOCANTE_PROGRAMACONV"))
  private Programa programaConvocatoria;

  /** Programa */
  @ManyToOne
  @JoinColumn(name = "programa_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADCONVOCANTE_PROGRAMA"))
  private Programa programa;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADCONVOCANTE_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;
}
