package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "requisito_equipo", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "convocatoria_id" }, name = "UK_REQUISITOEQUIPO_CONVOCATORIA") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RequisitoEquipo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisito_equipo_seq")
  @SequenceGenerator(name = "requisito_equipo_seq", sequenceName = "requisito_equipo_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Nivel académico */
  @Column(name = "nivel_academico_ref", length = 50)
  @Size(max = 50)
  private String nivelAcademicoRef;

  /** Años nivel académico */
  @Column(name = "anios_nivel_academico")
  @Min(0)
  private Integer aniosNivelAcademico;

  /** Edad máxima */
  @Column(name = "edad_maxima")
  @Min(0)
  private Integer edadMaxima;

  /** Ratio mujeres */
  @Column(name = "ratio_mujeres")
  @Min(0)
  private Integer ratioMujeres;

  /** Vinculación universidad */
  @Column(name = "vinculacion_universidad")
  private Boolean vinculacionUniversidad;

  /** Modalidad contrato */
  @Column(name = "modalidad_contrato_ref", length = 50)
  @Size(max = 50)
  private String modalidadContratoRef;

  /** Años vinculación */
  @Column(name = "anios_vinculacion")
  @Min(0)
  private Integer aniosVinculacion;

  /** Número mínimo competitivos */
  @Column(name = "num_minimo_competitivos")
  @Min(0)
  private Integer numMinimoCompetitivos;

  /** Número mínimo no competitivos */
  @Column(name = "num_minimo_no_competitivos")
  @Min(0)
  private Integer numMinimoNoCompetitivos;

  /** Número máximo competitivos */
  @Column(name = "num_maximo_competitivos_activos")
  @Min(0)
  private Integer numMaximoCompetitivosActivos;

  /** Número máximo no competitivos */
  @Column(name = "num_maximo_no_competitivos_activos")
  @Min(0)
  private Integer numMaximoNoCompetitivosActivos;

  /** Otros requisitos */
  @Column(name = "otros_requisitos", length = 2000)
  @Size(max = 2000)
  private String otrosRequisitos;

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOEQUIPO_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}
