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

  public enum Param {
    /**
     * Formato codigo partida presupuestaria
     * <code>formatoPartidaPresupuestaria</code>
     */
    FORMATO_PARTIDA_PRESUPUESTARIA("formatoPartidaPresupuestaria",
        "Expresión regular que se aplicará para la validación de las partidas presupuestarias de acuerdo al Sistema de gestión económica corporativo. Ejemplo: ^[A-Z0-9]{2}\\.[A-Z0-9]{4}\\.[A-Z0-9]{4}(\\.[A-Z0-9]{5,})$"),
    /**
     * Plantilla formato codigo partida presupuestaria
     * <code>plantillaFormatoPartidaPresupuestaria</code>
     */
    FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA("plantillaFormatoPartidaPresupuestaria",
        "Formato en el que se deben de introducir las partidas presupuestarias de acuerdo al Sistema de gestión económico corporativo. Ejemplo: XX.XXXX.XXXX.XXXXX"),
    /** Validacion gastos <code>validacionGastos</code> */
    VALIDACION_GASTOS("validacionGastos", "Activación del apartado Validación de gastos en Ejecución económica"),
    /**
     * Formato identificador justificacion
     * <code>formatoIdentificadorJustificacion</code>
     */
    FORMATO_IDENTIFICADOR_JUSTIFICACION("formatoIdentificadorJustificacion",
        "Expresión regular que se aplicará para la validación del identificador de justificación, de acuerdo al Sistema de gestión económica. Ejemplo: ^[0-9]{1,5}\\/[0-9]{4}$"),
    /**
     * Plantilla formato identificador justificacion
     * <code>plantillaFormatoIdentificadorJustificacion</code>
     */
    FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA("plantillaFormatoIdentificadorJustificacion",
        "Formato en el que se debe de introducir el campo identificador de justificación en el apartado Seguimiento de justificación de acuerdo al Sistema de gestión económica. Ejemplo: AAAA-YYYY"),
    /** Dedicacion minima grupo <code>dedicacionMinimaGrupo</code> */
    DEDICACION_MINIMA_GRUPO("dedicacionMinimaGrupo",
        "El valor porcentaje de dedicación de los miembros de los Grupos de investigación debe de superar este valor"),
    /** Formato codigo interno proyecto <code>formatoCodigoInternoProyecto</code> */
    FORMATO_CODIGO_INTERNO_PROYECTO("formatoCodigoInternoProyecto",
        "Expresión regular que se aplicará para la validación del campo referencia interna (cod Interno) del proyecto. Ejemplo: ^[A-Za-z0-9] {4}-[A-Za-z0-9]{3}-[A-Za-z0-9]{3}$"),
    /**
     * Plantilla formato codigo interno proyecto
     * <code>plantillaFormatoCodigoInternoProyecto</code>
     */
    FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA("plantillaFormatoCodigoInternoProyecto",
        "Formato en el que se debe de introducir el campo referencia interna del proyecto. Ejemplo: AAAA.YYY.YYY");

    private final String key;
    private final String description;

    private Param(String key, String description) {
      this.key = key;
      this.description = description;
    }

    public String getKey() {
      return this.key;
    }

    public String getDescription() {
      return this.description;
    }

    public static Param fromKey(String key) {
      for (Param param : Param.values()) {
        if (param.key.equals(key)) {
          return param;
        }
      }
      return null;
    }
  }

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

  public Object getParamValue(Param key) {
    switch (key) {
      case DEDICACION_MINIMA_GRUPO:
        return this.getDedicacionMinimaGrupo();
      case FORMATO_CODIGO_INTERNO_PROYECTO:
        return this.getFormatoCodigoInternoProyecto();
      case FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA:
        return this.getPlantillaFormatoCodigoInternoProyecto();
      case FORMATO_IDENTIFICADOR_JUSTIFICACION:
        return this.getFormatoIdentificadorJustificacion();
      case FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA:
        return this.getPlantillaFormatoIdentificadorJustificacion();
      case FORMATO_PARTIDA_PRESUPUESTARIA:
        return this.getFormatoPartidaPresupuestaria();
      case FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA:
        return this.getPlantillaFormatoPartidaPresupuestaria();
      case VALIDACION_GASTOS:
        return this.getValidacionGastos();
      default:
        return null;
    }
  }

  public void updateParamValue(Param key, String newValue) {
    switch (key) {
      case DEDICACION_MINIMA_GRUPO:
        this.setDedicacionMinimaGrupo(new BigDecimal(newValue));
        break;
      case FORMATO_CODIGO_INTERNO_PROYECTO:
        this.setFormatoCodigoInternoProyecto(newValue);
        break;
      case FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA:
        this.setPlantillaFormatoCodigoInternoProyecto(newValue);
        break;
      case FORMATO_IDENTIFICADOR_JUSTIFICACION:
        this.setFormatoIdentificadorJustificacion(newValue);
        break;
      case FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA:
        this.setPlantillaFormatoIdentificadorJustificacion(newValue);
        break;
      case FORMATO_PARTIDA_PRESUPUESTARIA:
        this.setFormatoPartidaPresupuestaria(newValue);
        break;
      case FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA:
        this.setPlantillaFormatoPartidaPresupuestaria(newValue);
        break;
      case VALIDACION_GASTOS:
        this.setValidacionGastos(new Boolean(newValue));
        break;
    }
  }
}