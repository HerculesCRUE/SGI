package org.crue.hercules.sgi.csp.model;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contexto_proyecto", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "proyecto_id" }, name = "UK_CONTEXTOPROYECTO_PROYECTO") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ContextoProyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Propiedad resultados */
  public enum PropiedadResultados {
    /** Sin resultados */
    SIN_RESULTADOS,
    /** Universidad */
    UNIVERSIDAD,
    /** Entidad financiadora */
    ENTIDAD_FINANCIADORA,
    /** Compartida */
    COMPARTIDA;
  }

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contexto_proyecto_seq")
  @SequenceGenerator(name = "contexto_proyecto_seq", sequenceName = "contexto_proyecto_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Objetivos */
  @Column(name = "objetivos", length = 2000, nullable = true)
  private String objetivos;

  /** Intereses */
  @Column(name = "intereses", length = 2000, nullable = true)
  private String intereses;

  /** Resultados previstos */
  @Column(name = "resultados_previstos", length = 2000, nullable = true)
  private String resultadosPrevistos;

  /** Propiedad resultados */
  @Column(name = "propiedad_resultados", length = 25, nullable = true)
  @Enumerated(EnumType.STRING)
  private PropiedadResultados propiedadResultados;

  /** AreaTematica convocatoria */
  @ManyToOne
  @JoinColumn(name = "area_tematica_convocatoria_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONTEXTOPROYECTO_AREATEMATICACONVOCATORIA"))
  private AreaTematica areaTematicaConvocatoria;

  /** AreaTematica */
  @ManyToOne
  @JoinColumn(name = "area_tematica_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONTEXTOPROYECTO_AREATEMATICA"))
  private AreaTematica areaTematica;

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONTEXTOPROYECTO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;
}
