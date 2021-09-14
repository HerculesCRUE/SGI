package org.crue.hercules.sgi.csp.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
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
@Table(name = "requisito_ip")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoIP extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id (compartido con Convocatoria) */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Número máximo ip */
  @Column(name = "num_maximo_ip", nullable = true)
  @Min(0)
  private Integer numMaximoIP;

  /** Fecha mínima obtención del nivel académico */
  @Column(name = "f_min_nivel_academico", nullable = true)
  private Instant fechaMinimaNivelAcademico;

  /** Fecha máxima obtención del nivel académico */
  @Column(name = "f_max_nivel_academico", nullable = true)
  private Instant fechaMaximaNivelAcademico;

  /** Edad máxima */
  @Column(name = "edad_maxima", nullable = true)
  @Min(0)
  private Integer edadMaxima;

  /** Referencia a la entidad externa Sexo del ESB */
  @Column(name = "sexo_ref", length = BaseEntity.EXTERNAL_REF_LENGTH, nullable = true)
  private String sexoRef;

  /** Vinculación universidad */
  @Column(name = "vinculacion_universidad", nullable = true)
  private Boolean vinculacionUniversidad;

  /** Fecha mínima obtención de la categoría profesional */
  @Column(name = "f_min_categoria_profesional", nullable = true)
  private Instant fechaMinimaCategoriaProfesional;

  /** Fecha máxima obtención de la categoría profesional */
  @Column(name = "f_max_categoria_profesional", nullable = true)
  private Instant fechaMaximaCategoriaProfesional;

  /** Número mínimo competitivos */
  @Column(name = "num_minimo_competitivos", nullable = true)
  @Min(0)
  private Integer numMinimoCompetitivos;

  /** Número mínimo no competitivos */
  @Column(name = "num_minimo_no_competitivos", nullable = true)
  @Min(0)
  private Integer numMinimoNoCompetitivos;

  /** Número máximo competitivos activos */
  @Column(name = "num_maximo_competitivos_activos", nullable = true)
  @Min(0)
  private Integer numMaximoCompetitivosActivos;

  /** Número máximo no competitivos activos */
  @Column(name = "num_maximo_no_competitivos_activos", nullable = true)
  @Min(0)
  private Integer numMaximoNoCompetitivosActivos;

  /** Otros requisitos */
  @Column(name = "otros_requisitos", length = 250, nullable = true)
  @Size(max = 250)
  private String otrosRequisitos;

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOIP_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;

  @OneToMany(mappedBy = "requisitoIP")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<RequisitoIPNivelAcademico> nivelesAcademicos = null;

  @OneToMany(mappedBy = "requisitoIP")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<RequisitoIPCategoriaProfesional> categoriasProfesionales = null;
}
