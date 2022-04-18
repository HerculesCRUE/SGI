package org.crue.hercules.sgi.prc.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
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

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.BaseEntity.Create;
import org.crue.hercules.sgi.prc.model.BaseEntity.Update;
import org.crue.hercules.sgi.prc.model.converter.EpigrafeCVNConverter;
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
  @Convert(converter = EpigrafeCVNConverter.class)
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
