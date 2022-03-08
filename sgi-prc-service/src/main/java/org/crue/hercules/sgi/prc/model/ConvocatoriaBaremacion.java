package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ConvocatoriaBaremacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaBaremacion extends BaseEntity {

  protected static final String TABLE_NAME = "convocatoria_baremacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = NOMBRE_CONVOCATORIA_LENGTH, nullable = false)
  private String nombre;

  @Column(name = "anio", nullable = false)
  private Integer anio;

  @Column(name = "anios_baremables", nullable = false)
  private Integer aniosBaremables;

  @Column(name = "ultimo_anio", nullable = false)
  private Integer ultimoAnio;

  @Column(name = "importe_total", nullable = false)
  private BigDecimal importeTotal;

  @Column(name = "partida_presupuestaria", length = PARTIDA_PRESUPUESTARIA_LENGTH, nullable = true)
  private String partidaPresupuestaria;

  @Column(name = "punto_produccion", nullable = true)
  private BigDecimal puntoProduccion;

  @Column(name = "punto_sexenio", nullable = true)
  private BigDecimal puntoSexenio;

  @Column(name = "punto_costes_indirectos", nullable = true)
  private BigDecimal puntoCostesIndirectos;

  @Column(name = "fecha_ejecucion", nullable = true)
  private Instant fechaEjecucion;

  @OneToMany(mappedBy = "convocatoriaBaremacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PuntuacionGrupo> puntuacionesGrupo = null;

  @OneToMany(mappedBy = "convocatoriaBaremacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Modulador> moduladores = null;

  @OneToMany(mappedBy = "convocatoriaBaremacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Rango> rangos = null;

  @OneToMany(mappedBy = "convocatoriaBaremacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProduccionCientifica> produccionesCientifica = null;

  @OneToMany(mappedBy = "convocatoriaBaremacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Baremo> baremos = null;

}
