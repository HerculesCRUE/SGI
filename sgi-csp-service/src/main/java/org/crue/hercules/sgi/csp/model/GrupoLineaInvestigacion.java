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

import org.crue.hercules.sgi.csp.validation.FechasGrupoLineaInvestigacionWithinGrupo;
import org.hibernate.validator.constraints.ScriptAssert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = GrupoLineaInvestigacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Validacion de fechas
@ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getFechaInicio() == null || #_this.getFechaFin() == null || #_this.getFechaFin().compareTo(#_this.getFechaInicio()) >= 0", reportOn = "fechaFin", message = "{org.crue.hercules.sgi.csp.validation.FechaInicialMayorFechaFinal.message}")
@FechasGrupoLineaInvestigacionWithinGrupo(groups = { BaseEntity.Create.class, BaseEntity.Update.class })
public class GrupoLineaInvestigacion extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_linea_investigacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoLineaInvestigacion.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoLineaInvestigacion.SEQUENCE_NAME, sequenceName = GrupoLineaInvestigacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  /** Linea Investigacion */
  @Column(name = "linea_investigacion_id", nullable = false)
  @NotNull
  private Long lineaInvestigacionId;

  /** Fecha inicio */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOLINEAINVESTIGACION_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "linea_investigacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOLINEAINVESTIGACION_LINEAINVESTIGACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final LineaInvestigacion lineaInvestigacion = null;

}
