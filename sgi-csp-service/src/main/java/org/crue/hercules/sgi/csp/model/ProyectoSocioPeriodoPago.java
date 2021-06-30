package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_socio_periodo_pago")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocioPeriodoPago extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_periodo_pago_seq")
  @SequenceGenerator(name = "proyecto_socio_periodo_pago_seq", sequenceName = "proyecto_socio_periodo_pago_seq", allocationSize = 1)
  private Long id;

  /** ProyectoSocio Id */
  @Column(name = "proyecto_socio_id", nullable = false)
  @NotNull
  private Long proyectoSocioId;

  /** Número de periodo */
  @Column(name = "num_periodo", nullable = false)
  @NotNull
  private Integer numPeriodo;

  /** Importe */
  @Column(name = "importe", nullable = true)
  private BigDecimal importe;

  /** Fecha prevista pago */
  @Column(name = "fecha_prevista_pago", nullable = false)
  @NotNull
  private Instant fechaPrevistaPago;

  /** Fecha pago */
  @Column(name = "fecha_pago", nullable = true)
  private Instant fechaPago;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_socio_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIOPERIODOPAGO_PROYECTOSOCIO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoSocio proyectoSocio = null;
}
