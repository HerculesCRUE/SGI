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
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EstadoMemoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_memoria_seq")
  @SequenceGenerator(name = "estado_memoria_seq", sequenceName = "estado_memoria_seq", allocationSize = 1)
  private Long id;

  /** Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ESTADOMEMORIA_MEMORIA"))
  @NotNull
  private Memoria memoria;

  /** Tipo estado memoria */
  @ManyToOne
  @JoinColumn(name = "tipo_estado_memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ESTADOMEMORIA_MEMORIA"))
  @NotNull
  private TipoEstadoMemoria tipoEstadoMemoria;

  /** Fecha estado. */
  @Column(name = "fecha_estado", nullable = false)
  @NotNull
  private Instant fechaEstado;

}