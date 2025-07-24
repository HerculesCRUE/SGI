package org.crue.hercules.sgi.eti.model;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
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

  /** Orden */
  @Column(name = "orden", nullable = false)
  @NotNull
  private Integer orden;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "bloque_nombre", joinColumns = @JoinColumn(name = "bloque_id"))
  @NotEmpty
  @Valid
  private Set<BloqueNombre> nombre;

}