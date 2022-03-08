package org.crue.hercules.sgi.prc.model;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.prc.exceptions.EpigrafeCVNNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity.Create;
import org.crue.hercules.sgi.prc.model.BaseEntity.Update;
import org.crue.hercules.sgi.prc.validation.UniqueFieldsValues;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ProduccionCientifica.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldsValues(groups = { Create.class, Update.class }, entityClass = ProduccionCientifica.class, fieldsNames = {
    ProduccionCientifica_.PRODUCCION_CIENTIFICA_REF, ProduccionCientifica_.EPIGRAFE_CV_N,
    ProduccionCientifica_.CONVOCATORIA_BAREMACION_ID })
public class ProduccionCientifica extends BaseEntity {

  protected static final String TABLE_NAME = "produccion_cientifica";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** EpigrafeCVN */
  public enum EpigrafeCVN {
    /** Publicaciones, documentos científicos y técnicos */
    E060_010_010_000("060.010.010.000"),
    /** Trabajos presentados en congresos nacionales o internacionales */
    E060_010_020_000("060.010.020.000"),
    /** Obras artísticas dirigidas */
    E050_020_030_000("050.020.030.000"),
    /** Consejos/comités editoriales */
    E060_030_030_000("060.030.030.000"),
    /** Invenciones */
    E050_030_010_000("050.030.010.000"),
    /** Contratos */
    E050_020_020_000("050.020.020.000"),
    /** Proyecto de investigación */
    E050_020_010_000("050.020.010.000"),
    /** Organización actividades I+D+i */
    E060_020_030_000("060.020.030.000"),
    /** Dirección de tesis */
    E030_040_000_000("030.040.000.000"),
    /** Sexenios (Periodos de actividad investigadora) */
    E060_030_070_000("060.030.070.000");

    private String internValue;

    private EpigrafeCVN(String internValue) {
      this.internValue = internValue;
    }

    public String getInternValue() {
      return internValue;
    }

    public static EpigrafeCVN getByInternValue(String internValue) {
      try {
        return Stream.of(EpigrafeCVN.values())
            .filter(epigrafeValue -> epigrafeValue.getInternValue().equalsIgnoreCase(internValue))
            .findFirst()
            .orElseThrow(() -> new EpigrafeCVNNotFoundException(internValue));

      } catch (Exception e) {
        throw new EpigrafeCVNNotFoundException(internValue);
      }
    }
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** idRef */
  @Column(name = "produccion_cientifica_ref", length = ID_REF_LENGTH, nullable = false)
  private String produccionCientificaRef;

  @Column(name = "epigrafe_cvn", length = EPIGRAFE_LENGTH, nullable = false)
  private EpigrafeCVN epigrafeCVN;

  /** EstadoProduccionCientifica */
  @ManyToOne
  @JoinColumn(name = "estado_produccion_cientifica_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PRODUCCIONCIENTIFICA_ESTADOPRODUCCIONCIENTIFICA"))
  private EstadoProduccionCientifica estado;

  @Column(name = "convocatoria_baremacion_id", nullable = true)
  private Long convocatoriaBaremacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_baremacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PRODUCCIONCIENTIFICA_CONVOCATORIABAREMACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaBaremacion convocatoriaBaremacion = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Proyecto> proyectos = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Autor> autores = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Acreditacion> acreditaciones = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<CampoProduccionCientifica> campos = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<IndiceImpacto> indicesImpacto = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PuntuacionItemInvestigador> puntuacionesItemInvestigador = null;

  @OneToMany(mappedBy = "produccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PuntuacionBaremoItem> puntuacionesBaremoItem = null;

}
