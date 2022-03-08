package org.crue.hercules.sgi.csp.model;

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
@Table(name = GrupoTipo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoTipo extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_tipo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public enum Tipo {
    /** Emergente */
    EMERGENTE,
    /** Consolidado */
    CONSOLIDADO,
    /** Precompetitivo */
    PRECOMPETITIVO,
    /** Grupo de alto rendimiento */
    ALTO_RENDIMIENTO
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoTipo.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoTipo.SEQUENCE_NAME, sequenceName = GrupoTipo.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Tipo */
  @Column(name = "tipo", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Tipo tipo;

  /** Fecha inicio */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOTIPO_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

}
