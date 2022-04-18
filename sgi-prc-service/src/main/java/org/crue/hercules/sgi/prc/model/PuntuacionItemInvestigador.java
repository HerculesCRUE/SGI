package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;
import java.util.List;

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
import javax.persistence.OneToMany;
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
@Table(name = PuntuacionItemInvestigador.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "persona_ref",
        "produccion_cientifica_id",
        "anio", "tipo_puntuacion" }, name = "UK_PUNTUACIONITEMINVESTIGADOR_PERSONA_PRCID_ANIO_TIPO_PUNT") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntuacionItemInvestigador extends BaseEntity {

  protected static final String TABLE_NAME = "puntuacion_item_investigador";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public enum TipoPuntuacion {
    LIBROS,
    ARTICULOS,
    SEXENIO,
    COMITES_EDITORIALES,
    CONGRESOS,
    DIRECCION_TESIS,
    OBRAS_ARTISTICAS,
    ORGANIZACION_ACTIVIDADES,
    CONTRATOS,
    PROYECTOS_INVESTIGACION,
    INVENCIONES,
    COSTE_INDIRECTO;
  }

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

  @Column(name = "persona_ref", length = PERSONA_REF_LENGTH, nullable = false)
  private String personaRef;

  /** anio */
  @Column(name = "anio", nullable = false)
  private Integer anio;

  @Column(name = "puntos", nullable = false)
  private BigDecimal puntos;

  @Column(name = "tipo_puntuacion", length = BaseEntity.TIPO_PUNTUACION_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoPuntuacion tipoPuntuacion;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONITEMINVESTIGADOR_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

  @OneToMany(mappedBy = "puntuacionItemInvestigador")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigador = null;

  @Override
  public String toString() {
    return "PuntuacionItemInvestigador [anio=" + anio + ", id=" + id + ", personaRef=" + personaRef
        + ", produccionCientificaId=" + produccionCientificaId + ", puntos=" + puntos + ", tipoPuntuacion="
        + tipoPuntuacion + "]";
  }

}
