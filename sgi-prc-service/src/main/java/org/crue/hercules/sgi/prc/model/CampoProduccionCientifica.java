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
import javax.persistence.UniqueConstraint;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.BaseEntity.Create;
import org.crue.hercules.sgi.prc.model.converter.CodigoCVNConverter;
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
@Table(name = CampoProduccionCientifica.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "codigo_cvn",
        "produccion_cientifica_id" }, name = "UK_CAMPOPRODUCCIONCIENTIFICA_CODIGOCVN_PRODUCCIONCIENTIFICAID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldsValues(groups = {
    Create.class }, entityClass = CampoProduccionCientifica.class, fieldsNames = {
        CampoProduccionCientifica_.CODIGO_CV_N, CampoProduccionCientifica_.PRODUCCION_CIENTIFICA_ID })
public class CampoProduccionCientifica extends BaseEntity {

  protected static final String TABLE_NAME = "campo_produccion_cientifica";
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

  /** CampoCVN */
  @Column(name = "codigo_cvn", length = CAMPO_CVN_LENGTH, nullable = false)
  @Convert(converter = CodigoCVNConverter.class)
  private CodigoCVN codigoCVN;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CAMPOPRODUCCIONCIENTIFICA_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

  @OneToMany(mappedBy = "campoProduccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ValorCampo> valoresCampos = null;

}
