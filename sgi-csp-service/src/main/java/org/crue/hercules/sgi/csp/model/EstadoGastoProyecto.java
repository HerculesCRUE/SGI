package org.crue.hercules.sgi.csp.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estado_gasto_proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoGastoProyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Tipos de estado gasto */
  public enum TipoEstadoGasto {
    /** Validado */
    VALIDADO,
    /** Bloqueado */
    BLOQUEADO,
    /** Rechazado */
    RECHAZADO;
  }

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_gasto_proyecto_seq")
  @SequenceGenerator(name = "estado_gasto_proyecto_seq", sequenceName = "estado_gasto_proyecto_seq", allocationSize = 1)
  private Long id;

  /** Gasto proyecto Id */
  @Column(name = "gasto_proyecto_id", nullable = false)
  private Long gastoProyectoId;

  /** Tipo estado gasto */
  @Column(name = "estado", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoEstadoGasto estado;

  /** Fecha estado */
  @Column(name = "fecha_estado", nullable = false)
  private Instant fechaEstado;

  /** Comentario */
  @Column(name = "comentario", length = 250, nullable = true)
  @Size(max = 250)
  private String comentario;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "gasto_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOGASTOPROYECTO_GASTOPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final GastoProyecto gastoProyecto = null;
}