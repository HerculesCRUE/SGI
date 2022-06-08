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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.validation.FechasGrupoLineaInvestigadorWithinGrupoLineaInvestigacion;
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
@Table(name = GrupoLineaInvestigador.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Validacion de fechas
@ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getFechaInicio() == null || #_this.getFechaFin() == null || #_this.getFechaFin().compareTo(#_this.getFechaInicio()) >= 0", reportOn = "fechaFin", message = "{org.crue.hercules.sgi.csp.validation.FechaInicialMayorFechaFinal.message}")
@FechasGrupoLineaInvestigadorWithinGrupoLineaInvestigacion(groups = { BaseEntity.Create.class,
    BaseEntity.Update.class })
public class GrupoLineaInvestigador extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_linea_investigador";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int PERSONA_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoLineaInvestigador.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoLineaInvestigador.SEQUENCE_NAME, sequenceName = GrupoLineaInvestigador.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Grupo Linea Investigacion */
  @Column(name = "grupo_linea_investigacion_id", nullable = false)
  @NotNull
  private Long grupoLineaInvestigacionId;

  /** Persona */
  @Column(name = "persona_ref", length = PERSONA_REF_LENGTH, nullable = false)
  @Size(max = PERSONA_REF_LENGTH)
  @NotBlank
  private String personaRef;

  /** Fecha inicio */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_linea_investigacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOLINEAINVESTIGADOR_GRUPOLINEAINVESTIGACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final GrupoLineaInvestigacion grupoLineaInvestigacion = null;

}
