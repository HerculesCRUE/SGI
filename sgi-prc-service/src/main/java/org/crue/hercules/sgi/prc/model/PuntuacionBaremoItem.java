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
@Table(name = PuntuacionBaremoItem.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "baremo_id",
        "produccion_cientifica_id" }, name = "UK_PUNTUACIONBAREMOITEM_BAREMOID_PRODUCCIONCIENTIFICAID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntuacionBaremoItem extends BaseEntity {

  protected static final String TABLE_NAME = "puntuacion_baremo_item";
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

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  /** Baremo Id */
  @Column(name = "baremo_id", nullable = false)
  private Long baremoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONBAREMOITEM_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "baremo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONBAREMOITEM_BAREMO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Baremo baremo = null;

}
