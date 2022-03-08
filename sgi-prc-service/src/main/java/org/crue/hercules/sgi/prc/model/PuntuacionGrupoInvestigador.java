package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = PuntuacionGrupoInvestigador.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "persona_ref",
        "puntuacion_grupo_id" }, name = "UK_PUNTUACIONGRUPOINVESTIGADOR_PERSONAREF_PUNTUACIONGRUPOID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntuacionGrupoInvestigador extends BaseEntity {

  protected static final String TABLE_NAME = "puntuacion_grupo_investigador";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** personaRef */
  @Column(name = "persona_ref", nullable = false)
  private Long personaRef;

  /** PuntuacionGrupo Id */
  @Column(name = "puntuacion_grupo_id", nullable = false)
  private Long puntuacionGrupoId;

  @Column(name = "puntos", nullable = false)
  private BigDecimal puntos;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "puntuacion_grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONGRUPOINVESTIGADOR_PUNTUACIONGRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final PuntuacionGrupo puntuacionGrupo = null;

}
