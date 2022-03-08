package org.crue.hercules.sgi.prc.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TablaEditorial.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TablaEditorial extends BaseActivableEntity implements Serializable {

  protected static final String TABLE_NAME = "tabla_editorial";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Fecha Inicio */
  @Column(name = "fecha_inicio", nullable = false)
  private Instant fechaInicio;

  /** Fecha Fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  @OneToMany(mappedBy = "tablaEditorial")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EditorialPrestigio> editorialesPrestigio = null;
}
