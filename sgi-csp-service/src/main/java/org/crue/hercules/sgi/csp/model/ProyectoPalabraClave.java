package org.crue.hercules.sgi.csp.model;

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
@Table(name = "proyecto_palabra_clave")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoPalabraClave extends BaseEntity {
  public static final int REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_palabra_clave_seq")
  @SequenceGenerator(name = "proyecto_palabra_clave_seq", sequenceName = "proyecto_palabra_clave_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id. */
  @Column(name = "proyecto_id", nullable = false)
  private Long proyectoId;

  /** Palabra Clave Ref */
  @Column(name = "palabra_clave_ref", length = REF_LENGTH, nullable = false)
  private String palabraClaveRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPALABRACLAVE_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;
}
