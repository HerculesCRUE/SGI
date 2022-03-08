package org.crue.hercules.sgi.prc.model;

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
@Table(name = Proyecto.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "proyecto_ref",
        "produccion_cientifica_id" }, name = "UK_PROYECTO_PROYECTOREF_PRODUCCIONCIENTIFICAID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto extends BaseEntity {

  protected static final String TABLE_NAME = "proyecto";
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

  /** proyectoRef */
  @Column(name = "proyecto_ref", nullable = false)
  private Long proyectoRef;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

}
