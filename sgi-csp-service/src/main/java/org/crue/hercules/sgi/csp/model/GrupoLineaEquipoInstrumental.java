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
@Table(name = GrupoLineaEquipoInstrumental.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoLineaEquipoInstrumental extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_linea_equipo_instrumental";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int PERSONA_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoLineaEquipoInstrumental.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoLineaEquipoInstrumental.SEQUENCE_NAME, sequenceName = GrupoLineaEquipoInstrumental.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Grupo Linea Investigacion */
  @Column(name = "grupo_linea_investigacion_id", nullable = false)
  @NotNull
  private Long grupoLineaInvestigacionId;

  /** Grupo Equipo Instrumental */
  @Column(name = "grupo_equipo_instrumental_id", nullable = false)
  @NotNull
  private Long grupoEquipoInstrumentalId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_linea_investigacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOLINEAEQUIPOINSTRUMENTAL_GRUPOLINEAINVESTIGACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final GrupoLineaInvestigacion grupoLineaInvestigacion = null;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_equipo_instrumental_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOLINEAEQUIPOINSTRUMENTAL_GRUPOEQUIPOINSTRUMENTAL"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final GrupoEquipoInstrumental grupoEquipoInstrumental = null;

}
