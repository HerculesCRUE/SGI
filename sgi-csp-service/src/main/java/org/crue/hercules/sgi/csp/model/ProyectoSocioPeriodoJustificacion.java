package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
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
@Table(name = "proyecto_socio_periodo_justificacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocioPeriodoJustificacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_periodo_justificacion_seq")
  @SequenceGenerator(name = "proyecto_socio_periodo_justificacion_seq", sequenceName = "proyecto_socio_periodo_justificacion_seq", allocationSize = 1)
  private Long id;

  /** ProyectoSocio Id */
  @Column(name = "proyecto_socio_id", nullable = false)
  @NotNull
  private Long proyectoSocioId;

  /** Número periodo. */
  @Column(name = "num_periodo", nullable = false)
  private Integer numPeriodo;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = false)
  @NotNull
  private Instant fechaFin;

  /** Fecha Inicio Presentación. */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private Instant fechaInicioPresentacion;

  /** Fecha Fin Presentación. */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private Instant fechaFinPresentacion;

  /** Observaciones. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "proyecto_socio_periodo_justificacion_observaciones", joinColumns = @JoinColumn(name = "proyecto_socio_periodo_justificacion_id"))
  @Valid
  @Builder.Default
  private Set<ProyectoSocioPeriodoJustificacionObservaciones> observaciones = new HashSet<>();

  /** Documentación recibida. */
  @Column(name = "doc_recibida", nullable = true)
  private Boolean documentacionRecibida;

  /** Fecha Recepcion. */
  @Column(name = "fecha_recepcion", nullable = true)
  private Instant fechaRecepcion;

  /** Importe justificado. */
  @Column(name = "importe_justificado", nullable = true)
  private BigDecimal importeJustificado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_socio_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIOPERIODOJUSTIFICACION_PROYECTOSOCIO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoSocio proyectoSocio = null;

  @OneToMany(mappedBy = "proyectoSocioPeriodoJustificacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoSocioPeriodoJustificacionDocumento> documentos = null;

}