package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
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
@Table(name = "proyecto_prorroga")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoProrroga extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Tipo de prórroga. */
  public enum Tipo {
    /** Tiempo */
    TIEMPO,
    /** Importe */
    IMPORTE,
    /** Tiempo e importe */
    TIEMPO_IMPORTE;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_prorroga_seq")
  @SequenceGenerator(name = "proyecto_prorroga_seq", sequenceName = "proyecto_prorroga_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Número prórroga */
  @Column(name = "num_prorroga")
  @NotNull
  @Min(1)
  private Integer numProrroga;

  /** Fecha concesión */
  @Column(name = "fecha_concesion", nullable = false)
  @NotNull
  private Instant fechaConcesion;

  /** Tipo prórroga */
  @Column(name = "tipo", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Tipo tipo;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Importe */
  @Column(name = "importe", nullable = true)
  @Min(0)
  private BigDecimal importe;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000)
  @Size(max = 2000)
  private String observaciones;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPRORROGA_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  @OneToMany(mappedBy = "proyectoProrroga")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProrrogaDocumento> documentos = null;
}