package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.GrupoEquipo.OnDelete;
import org.crue.hercules.sgi.csp.validation.FechasGrupoEquipoWithinGrupo;
import org.crue.hercules.sgi.csp.validation.NotGrupoLineaInvestigadorInGrupoEquipo;
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
@Table(name = GrupoEquipo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Validacion de fechas
@ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getFechaInicio() == null || #_this.getFechaFin() == null || #_this.getFechaFin().compareTo(#_this.getFechaInicio()) >= 0", reportOn = "fechaFin", message = "{org.crue.hercules.sgi.csp.validation.FechaInicialMayorFechaFinal.message}")
@FechasGrupoEquipoWithinGrupo(groups = { BaseEntity.Create.class, BaseEntity.Update.class })
@NotGrupoLineaInvestigadorInGrupoEquipo(groups = { OnDelete.class })
public class GrupoEquipo extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_equipo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int PERSONA_REF_LENGTH = 50;
  public static final int PARTICIPACION_MIN = 0;
  public static final int PARTICIPACION_MAX = 100;

  public enum Dedicacion {
    /** Completa */
    COMPLETA,
    /** Parcial */
    PARCIAL
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoEquipo.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoEquipo.SEQUENCE_NAME, sequenceName = GrupoEquipo.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

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

  /** Rol proyecto. */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_GRUPOEQUIPO_ROLPROYECTO"))
  @NotNull
  private RolProyecto rol;

  /** Dedicacion */
  @Column(name = "dedicacion", nullable = true)
  @Enumerated(EnumType.STRING)
  private Dedicacion dedicacion;

  /** Participacion */
  @Column(name = "participacion", nullable = true)
  @Min(PARTICIPACION_MIN)
  @Max(PARTICIPACION_MAX)
  private BigDecimal participacion;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOEQUIPO_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

  /**
   * Interfaz para marcar validaciones al eliminar la entidad
   */
  public interface OnDelete {

  }
}
