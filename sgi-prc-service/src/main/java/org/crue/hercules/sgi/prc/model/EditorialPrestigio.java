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
@Table(name = EditorialPrestigio.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "area_ref",
        "tabla_editorial_id" }, name = "UK_EDITORIALPRESTIGIO_AREAREF_TABLAEDITORIALID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorialPrestigio extends BaseEntity {

  protected static final String TABLE_NAME = "editorial_prestigio";
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

  /** areaRef */
  @Column(name = "area_ref", length = AREA_REF_LENGTH, nullable = false)
  private String areaRef;

  @Column(name = "nombre", length = NOMBRE_EDITORIAL_LENGTH, nullable = false)
  private String nombre;

  /** TablaEditorial Id */
  @Column(name = "tabla_editorial_id", nullable = false)
  private Long tablaEditorialId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "tabla_editorial_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EDITORIALPRESTIGIO_TABLAEDITORIAL"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final TablaEditorial tablaEditorial = null;

}
