package org.crue.hercules.sgi.eti.model;

import java.time.Instant;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_acta")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EstadoActa extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_acta_seq")
  @SequenceGenerator(name = "estado_acta_seq", sequenceName = "estado_acta_seq", allocationSize = 1)
  private Long id;

  /** Acta */
  @ManyToOne
  @JoinColumn(name = "acta_id", nullable = true, foreignKey = @ForeignKey(name = "FK_ESTADOACTA_ACTA"))
  private Acta acta;

  /** Tipo Estado Acta */
  @ManyToOne
  @JoinColumn(name = "tipo_estado_acta_id", nullable = true, foreignKey = @ForeignKey(name = "FK_ESTADOACTA_TIPOESTADOACTA"))
  private TipoEstadoActa tipoEstadoActa;

  /** Fecha Estado */
  @Column(name = "fecha_estado", nullable = false)
  private Instant fechaEstado;

}