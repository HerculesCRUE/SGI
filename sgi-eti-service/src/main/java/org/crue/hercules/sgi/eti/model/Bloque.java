package org.crue.hercules.sgi.eti.model;

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
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Bloque
 */

@Entity
@Table(name = "bloque")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Bloque extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bloque_seq")
  @SequenceGenerator(name = "bloque_seq", sequenceName = "bloque_seq", allocationSize = 1)
  private Long id;

  /** Formulario */
  @ManyToOne
  @JoinColumn(name = "formulario_id", nullable = true, foreignKey = @ForeignKey(name = "FK_BLOQUE_FORMULARIO"))
  private Formulario formulario;

  /** Nombre */
  @Column(name = "nombre", length = 2000, nullable = false)
  private String nombre;

  /** Orden */
  @Column(name = "orden", nullable = false)
  @NotNull
  private Integer orden;

}