package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Configuracion
 */

@Entity
@Table(name = "configuracion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuracion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracion_seq")
  @SequenceGenerator(name = "configuracion_seq", sequenceName = "configuracion_seq", allocationSize = 1)
  private Long id;

  /** Formato codigo partida presupuestaria. */
  @Column(name = "formato_partida_presupuestaria", nullable = false, unique = true)
  @NotNull
  private String formatoPartidaPresupuestaria;

  /** Formato codigo partida presupuestaria. */
  @Column(name = "plantilla_formato_partida_presupuestaria", nullable = false, unique = true)
  @NotNull
  private String plantillaFormatoPartidaPresupuestaria;

  /** Validacion gastos */
  @Column(name = "validacion_gastos", columnDefinition = "boolean default false", nullable = true)
  private Boolean validacionGastos;

  /** Formato identificador justificacion. */
  @Column(name = "formato_identificador_justificacion", nullable = true, unique = true)
  private String formatoIdentificadorJustificacion;

  /** Plantilla formato identificador justificacion. */
  @Column(name = "plantilla_formato_identificador_justificacion", nullable = true, unique = true)
  private String plantillaFormatoIdentificadorJustificacion;

  /** Dedicacion minima grupo. */
  @Column(name = "dedicacion_minima_grupo", nullable = true, unique = true)
  private BigDecimal dedicacionMinimaGrupo;

  /** Formato codigo interno proyecto. */
  @Column(name = "formato_codigo_interno_proyecto", nullable = true, unique = true)
  private String formatoCodigoInternoProyecto;

  /** Plantilla formato codigo interno proyecto. */
  @Column(name = "plantilla_formato_codigo_interno_proyecto", nullable = true, unique = true)
  private String plantillaFormatoCodigoInternoProyecto;
}