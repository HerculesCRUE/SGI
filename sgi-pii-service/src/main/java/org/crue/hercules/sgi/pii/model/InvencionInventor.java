package org.crue.hercules.sgi.pii.model;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invencion_inventor")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvencionInventor extends BaseEntity {
  public static final int REF_LENGTH = 50;
  public static final int PARTICIPACION_MIN = 1;
  public static final int PARTICIPACION_MAX = 100;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invencion_inventor_seq")
  @SequenceGenerator(name = "invencion_inventor_seq", sequenceName = "invencion_inventor_seq", allocationSize = 1)
  private Long id;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Persona ref */
  @Column(name = "inventor_ref", length = REF_LENGTH, nullable = false)
  private String inventorRef;

  /** Participación */
  @Column(name = "participacion", nullable = false)
  private Integer participacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONINVENTOR_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;
}
