package org.crue.hercules.sgi.eti.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "apartado")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apartado extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apartado_seq")
  @SequenceGenerator(name = "apartado_seq", sequenceName = "apartado_seq", allocationSize = 1)
  private Long id;

  /** Bloque. */
  @ManyToOne
  @JoinColumn(name = "bloque_id", nullable = false, foreignKey = @ForeignKey(name = "FK_APARTADO_BLOQUE"))
  private Bloque bloque;

  /** Apartado Formulario Padre. */
  @ManyToOne
  @JoinColumn(name = "padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_APARTADO_PADRE"))
  private Apartado padre;

  /** Orden. */
  @Column(name = "orden", nullable = false)
  private Integer orden;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "apartado_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ApartadoDefinicion> definicion;

}
