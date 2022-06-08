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
@Table(name = GrupoLineaClasificacion.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "grupo_linea_investigacion_id",
        "clasificacion_ref" }, name = "UK_GRUPOLINEACLASIFICACION_GRUPOLINEAINVESTIGACION_CLASIFICACION") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoLineaClasificacion extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_linea_clasificacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int CLASIFICACION_REF_LENGTH = 50;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoLineaClasificacion.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoLineaClasificacion.SEQUENCE_NAME, sequenceName = GrupoLineaClasificacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Grupo Linea Investigacion Id */
  @Column(name = "grupo_linea_investigacion_id", nullable = false)
  @NotNull
  private Long grupoLineaInvestigacionId;

  /** Clasificacion */
  @Column(name = "clasificacion_ref", length = GrupoLineaClasificacion.CLASIFICACION_REF_LENGTH, nullable = false)
  @NotEmpty
  @Size(max = GrupoLineaClasificacion.CLASIFICACION_REF_LENGTH)
  private String clasificacionRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_linea_investigacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOLINEACLASIFICACION_GRUPOLINEAINVESTIGACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final GrupoLineaInvestigacion grupoLineaInvestigacion = null;
}
