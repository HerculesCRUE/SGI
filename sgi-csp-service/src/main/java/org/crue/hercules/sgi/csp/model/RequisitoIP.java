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
@Table(name = "requisito_ip", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "convocatoria_id" }, name = "UK_REQUISITOIP_CONVOCATORIA") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RequisitoIP extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisito_ip_seq")
  @SequenceGenerator(name = "requisito_ip_seq", sequenceName = "requisito_ip_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Número máximo ip */
  @Column(name = "num_maximo_ip", nullable = true)
  @Min(0)
  private Integer numMaximoIP;

  /** Ref nivel académico. */
  @Column(name = "nivel_academico_ref", length = 50, nullable = true)
  @Size(max = 50)
  private String nivelAcademicoRef;

  /** Años nivel académico */
  @Column(name = "anios_nivel_academico", nullable = true)
  @Min(0)
  private Integer aniosNivelAcademico;

  /** Edad máxima */
  @Column(name = "edad_maxima", nullable = true)
  @Min(0)
  private Integer edadMaxima;

  /** Sexo. */
  @Column(name = "sexo", length = 50, nullable = true)
  @Size(max = 50)
  private String sexo;

  /** Vinculación universidad */
  @Column(name = "vinculacion_universidad", nullable = true)
  private Boolean vinculacionUniversidad;

  /** Ref modalidad contrato. */
  @Column(name = "modalidad_contrato_ref", length = 50, nullable = true)
  @Size(max = 50)
  private String modalidadContratoRef;

  /** Años vinculación */
  @Column(name = "anios_vinculacion", nullable = true)
  @Min(0)
  private Integer aniosVinculacion;

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
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOIP_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}
