package org.crue.hercules.sgi.eti.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

  /** Version */
  @Column(name = "version", nullable = false)
  private Integer version;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "formly_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<FormlyDefinicion> definicion;

}