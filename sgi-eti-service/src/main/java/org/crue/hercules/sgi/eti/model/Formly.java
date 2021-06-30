package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * La entidad Formly representa un formulario configurable.
 */
@Entity
@Table(name = "formly", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "nombre", "version" }, name = "UK_FORMLY_NOMBRE_VERSION") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formly extends BaseEntity {
  public static final int NOMBRE_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formly_seq")
  @SequenceGenerator(name = "formly_seq", sequenceName = "formly_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

  /** Version */
  @Column(name = "version", nullable = false)
  private Integer version;

  /** Esquema. */
  @Column(name = "esquema", nullable = false, columnDefinition = "clob")
  private String esquema;

}