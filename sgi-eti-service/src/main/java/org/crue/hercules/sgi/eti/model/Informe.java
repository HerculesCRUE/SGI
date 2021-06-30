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
 * Informe
 */

@Entity
@Table(name = "informe")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Informe extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "informe_seq")
  @SequenceGenerator(name = "informe_seq", sequenceName = "informe_seq", allocationSize = 1)
  private Long id;

  /** Formulario Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = true, foreignKey = @ForeignKey(name = "FK_INFORME_MEMORIA"))
  private Memoria memoria;

  /** Referencia documento */
  @Column(name = "documento_ref", length = 250, nullable = false)
  private String documentoRef;

  /** Version */
  @Column(name = "version", nullable = false)
  @NotNull
  private Integer version;

  /** Tipo evaluacion. */
  @ManyToOne
  @JoinColumn(name = "tipo_evaluacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_INFORME_TIPOEVALUACION"))
  private TipoEvaluacion tipoEvaluacion;

}