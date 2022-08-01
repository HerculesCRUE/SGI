package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TipoRequerimiento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class TipoRequerimiento extends BaseActivableEntity {

  protected static final String TABLE_NAME = "tipo_requerimiento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TipoRequerimiento.SEQUENCE_NAME)
  @SequenceGenerator(name = TipoRequerimiento.SEQUENCE_NAME, sequenceName = TipoRequerimiento.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", nullable = false)
  private String nombre;
}
