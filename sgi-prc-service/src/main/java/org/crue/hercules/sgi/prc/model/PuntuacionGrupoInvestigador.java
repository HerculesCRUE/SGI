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
        "puntuacion_grupo_id",
        "puntuacion_item_investigador_id" }, name = "UK_PUNTUACIONGRUPOINVESTIGADOR_PUNTGRUPOID_PUNTITEMINVID") })
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

  @Column(name = "puntos", nullable = false)
  private BigDecimal puntos;

  /** PuntuacionGrupo Id */
  @Column(name = "puntuacion_grupo_id", nullable = false)
  private Long puntuacionGrupoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "puntuacion_grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONGRUPOINVESTIGADOR_PUNTUACIONGRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final PuntuacionGrupo puntuacionGrupo = null;

  /** PuntuacionItemInvestigador Id */
  @Column(name = "puntuacion_item_investigador_id", nullable = true)
  private Long puntuacionItemInvestigadorId;

  @ManyToOne
  @JoinColumn(name = "puntuacion_item_investigador_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONGRUPOINVESTIGADOR_PUNTUACIONITEMINVESTIGADOR"))
  private PuntuacionItemInvestigador puntuacionItemInvestigador;

  @Override
  public String toString() {
    return "PuntuacionGrupoInvestigador [id=" + id + ", puntos=" + puntos + ", puntuacionGrupoId=" + puntuacionGrupoId
        + ", puntuacionItemInvestigadorId=" + puntuacionItemInvestigadorId + "]";
  }

}
