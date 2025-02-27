package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

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

  public enum ValidacionClasificacionGastos {
    VALIDACION,
    CLASIFICACION,
    ELEGIBILIDAD;
  }

  public enum ModoEjecucion {
    SINCRONA,
    ASINCRONA;
  }

  public enum CardinalidadRelacionSgiSge {
    SGI_1_SGE_1,
    SGI_1_SGE_N,
    SGI_N_SGE_1,
    SGI_N_SGE_N;
  }

  public enum SgeFacturasJustificantesColumnasFijas {
    ANUALIDAD,
    PROYECTO,
    CONCEPTO_GASTO,
    CLASIFICACION_SGE,
    APLICACION_PRESUPUESTARIA,
    CODIGO_ECONOMICO,
    FECHA_DEVENGO
  }

  public enum Param {
    /**
     * Formato codigo partida presupuestaria
     * <code>formatoPartidaPresupuestaria</code>
     */
    FORMATO_PARTIDA_PRESUPUESTARIA("formatoPartidaPresupuestaria"),
    /**
     * Plantilla formato codigo partida presupuestaria
     * <code>plantillaFormatoPartidaPresupuestaria</code>
     */
    FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA("plantillaFormatoPartidaPresupuestaria"),
    /** Validacion gastos <code>validacionClasificacionGastos</code> */
    VALIDACION_CLASIFICACION_GASTOS("validacionClasificacionGastos"),
    /**
     * Formato identificador justificacion
     * <code>formatoIdentificadorJustificacion</code>
     */
    FORMATO_IDENTIFICADOR_JUSTIFICACION("formatoIdentificadorJustificacion"),
    /**
     * Plantilla formato identificador justificacion
     * <code>plantillaFormatoIdentificadorJustificacion</code>
     */
    FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA("plantillaFormatoIdentificadorJustificacion"),
    /** Dedicacion minima grupo <code>dedicacionMinimaGrupo</code> */
    DEDICACION_MINIMA_GRUPO("dedicacionMinimaGrupo"),
    /** Formato codigo interno proyecto <code>formatoCodigoInternoProyecto</code> */
    FORMATO_CODIGO_INTERNO_PROYECTO("formatoCodigoInternoProyecto"),
    /**
     * Plantilla formato codigo interno proyecto
     * <code>plantillaFormatoCodigoInternoProyecto</code>
     */
    FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA("plantillaFormatoCodigoInternoProyecto"),
    /**
     * Habilitar Ejecución económica de Grupos de investigación
     * <code>ejecucionEconomicaGruposEnabled</code>
     */
    EJECUCION_ECONOMICA_GRUPOS_ENABLED("ejecucionEconomicaGruposEnabled"),
    /**
     * Cardinalidad relación proyecto SGI - identificador SGE
     * <code>cardinalidadRelacionSgiSge</code>
     */
    CARDINALIDAD_RELACION_SGI_SGE("cardinalidadRelacionSgiSge"),
    /**
     * Habilitar creación de Partidas presupuestarias en el SGE
     * <code>partidasPresupuestariasSGE</code>
     */
    PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED("partidasPresupuestariasSgeEnabled"),
    /**
     * Habilitar creación de Periodos de amortización en el SGE
     * <code>amortizacionFondosSGE</code>
     */
    AMORTIZACION_FONDOS_SGE_ENABLED("amortizacionFondosSgeEnabled"),
    /**
     * Habilitar buscador proyectos económicos pantalla Configuración Económica -
     * Identificación
     * <code>altaBuscadorSge</code>
     */
    ALTA_BUSCADOR_SGE_ENABLED("altaBuscadorSgeEnabled"),
    /**
     * Habilitar la integración de gastos justificados (apartado seguimiento de
     * justificación).
     */
    GASTOS_JUSTIFICADOS_SGE_ENABLED("gastosJustificadosSgeEnabled"),
    /**
     * Habilitar la acción de solicitar modificación de los datos del proyecto SGE
     */
    MODIFICACION_PROYECTO_SGE_ENABLED("modificacionProyectoSgeEnabled"),
    /**
     * Habilitar la visualización del campo Sector IVA proveniente de la integración
     * con el SGE
     */
    SECTOR_IVA_SGE_ENABLED("sectorIvaSgeEnabled"),
    /**
     * Habilitar la visualización de la la opción de menú "Modificaciones" dentro de
     * "Ejecución económica - Detalle de operaciones"
     */
    DETALLE_OPERACIONES_MODIFICACIONES_ENABLED("detalleOperacionesModificacionesEnabled"),
    /**
     * Determina si el alta del proyecto económico en el SGE se realiza de forma
     * sincrona o de forma asíncrona
     */
    PROYECTO_SGE_ALTA_MODO_EJECUCION("proyectoSgeAltaModoEjecucion"),
    /**
     * Determina si la modificacion del proyecto económico en el SGE se realiza de
     * forma sincrona o de forma asíncrona
     */
    PROYECTO_SGE_MODIFICACION_MODO_EJECUCION("proyectoSgeModificacionModoEjecucion"),
    /**
     * Determina si hay integración del calendario facturación con el SGE para
     * indicar si se van a notificar las facturas previstas validadas del calendario
     * de facturación al SGE
     */
    CALENDARIO_FACTURACION_SGE_ENABLED("calendarioFacturacionSgeEnabled"),
    /**
     * Columnas a mostrar en Facturas y gastos (ejecución económica - facturas y
     * justificantes)
     */
    FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES("facturasGastosColumnasFijasVisibles"),
    /**
     * Columnas a mostrar en Viajes y dietas (ejecución económica - facturas y
     * justificantes)
     */
    VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES("viajesDietasColumnasFijasVisibles"),
    /**
     * Columnas a mostrar en Personal Contratado (ejecución económica - facturas y
     * justificantes)
     */
    PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES("personalContratadoColumnasFijasVisibles"),
    /**
     * Determina si se esta habilitado el filtro de proyectos con algun socio del
     * pais seleccionado
     */
    PROYECTO_SOCIO_PAIS_FILTER_ENABLED("proyectoSocioPaisFilterEnabled");

    private final String key;

    private Param(String key) {
      this.key = key;
    }

    public String getKey() {
      return this.key;
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

  /** Validacion/Clasificacion gastos */
  @Column(name = "validacion_clasificacion_gastos", nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private ValidacionClasificacionGastos validacionClasificacionGastos;

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

  /** Habilitar Ejecución económica de Grupos de investigación */
  @Column(name = "gin_ejecucion_economica", columnDefinition = "boolean default true", nullable = true, unique = true)
  private Boolean ejecucionEconomicaGruposEnabled;

  /** Cardinalidad relación proyecto SGI - identificador SGE */
  @Column(name = "cardinalidad_relacion_sgi_sge", nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private CardinalidadRelacionSgiSge cardinalidadRelacionSgiSge;

  /** Habilitar creación de Partidas presupuestarias en el SGE */
  @Column(name = "partidas_presupuestarias_sge", columnDefinition = "boolean default false", nullable = true, unique = true)
  private Boolean partidasPresupuestariasSGE;

  /** Habilitar creación de periodos de amortización de fondos en el SGE */
  @Column(name = "sge_amortizacion_fondos", columnDefinition = "boolean default false", nullable = false, unique = true)
  private Boolean amortizacionFondosSGE;

  /**
   * Habilitar que se muestre el buscador de proyectos económicos al pulsar el
   * botón de "Añadir identificador SGE" en la pantalla de
   * "Configuración económica - Identificación"
   */
  @Column(name = "sge_alta_buscador", columnDefinition = "boolean default false", nullable = false, unique = true)
  private Boolean altaBuscadorSGE;

  /**
   * Habilitar la integración de gastos justificados (apartado seguimiento de
   * justificación).
   */
  @Column(name = "sge_gastos_justificados", columnDefinition = "boolean default false", nullable = false, unique = true)
  private Boolean gastosJustificadosSGE;

  /**
   * Habilitar la acción de solicitar modificación de los datos del proyecto SGE
   */
  @Column(name = "sge_modificacion", columnDefinition = "boolean default false", nullable = false, unique = true)
  private Boolean modificacionProyectoSge;

  /**
   * Habilitar la visualización del campo Sector IVA proveniente de la integración
   * con el SGE
   */
  @Column(name = "sge_sector_iva", columnDefinition = "boolean default true", nullable = false, unique = true)
  private Boolean sectorIvaSgeEnabled;

  /**
   * Habilitar la visualización de la la opción de menú "Modificaciones" dentro de
   * "Ejecución económica - Detalle de operaciones"
   */
  @Column(name = "sge_modificaciones", columnDefinition = "boolean default true", nullable = false, unique = true)
  private Boolean detalleOperacionesModificacionesEnabled;

  /**
   * Determina si el alta del proyecto económico en el SGE se realiza de forma
   * sincrona o de forma asíncrona
   */
  @Column(name = "sge_sincronizacion_alta_proyecto", nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private ModoEjecucion proyectoSgeAltaModoEjecucion;

  /**
   * Determina si la modificacion del proyecto económico en el SGE se realiza de
   * forma sincrona o de forma asíncrona
   */
  @Column(name = "sge_sincronizacion_modificacion_proyecto", nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private ModoEjecucion proyectoSgeModificacionModoEjecucion;

  /**
   * Columnas a mostrar en Facturas y gastos (ejecución económica - facturas y
   * justificantes)
   */
  @Column(name = "sge_facturas_columnas_visibles", nullable = true, unique = true)
  private String facturasGastosColumnasFijasVisibles;

  /**
   * Columnas a mostrar en Viajes y dietas (ejecución económica - facturas y
   * justificantes)
   */
  @Column(name = "sge_viajes_columnas_visibles", nullable = true, unique = true)
  private String viajesDietasColumnasFijasVisibles;

  /**
   * Columnas a mostrar en Personal Contratado (ejecución económica - facturas y
   * justificantes)
   */
  @Column(name = "sge_personal_columnas_visibles", nullable = true, unique = true)
  private String personalContratadoColumnasFijasVisibles;

  /**
   * Determina si hay integración del calendario facturación con el SGE para
   * indicar si se van a notificar las facturas previstas validadas del calendario
   * de facturación al SGE
   */
  @Column(name = "sge_calendario_facturacion", columnDefinition = "boolean default true", nullable = false, unique = true)
  private Boolean calendarioFacturacionSgeEnabled;

  /**
   * Determina si se esta habilitado el filtro de proyectos con algun socio del
   * pais seleccionado
   */
  @Column(name = "csp_pro_socio_pais_filter_enabled", columnDefinition = "boolean default true", nullable = false, unique = true)
  private Boolean proyectoSocioPaisFilterEnabled;

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
      case VALIDACION_CLASIFICACION_GASTOS:
        return this.getValidacionClasificacionGastos();
      case EJECUCION_ECONOMICA_GRUPOS_ENABLED:
        return this.getEjecucionEconomicaGruposEnabled();
      case CARDINALIDAD_RELACION_SGI_SGE:
        return this.getCardinalidadRelacionSgiSge();
      case PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED:
        return this.getPartidasPresupuestariasSGE();
      case AMORTIZACION_FONDOS_SGE_ENABLED:
        return this.getAmortizacionFondosSGE();
      case ALTA_BUSCADOR_SGE_ENABLED:
        return this.getAltaBuscadorSGE();
      case GASTOS_JUSTIFICADOS_SGE_ENABLED:
        return this.getGastosJustificadosSGE();
      case MODIFICACION_PROYECTO_SGE_ENABLED:
        return this.getModificacionProyectoSge();
      case SECTOR_IVA_SGE_ENABLED:
        return this.getSectorIvaSgeEnabled();
      case DETALLE_OPERACIONES_MODIFICACIONES_ENABLED:
        return this.getDetalleOperacionesModificacionesEnabled();
      case PROYECTO_SGE_ALTA_MODO_EJECUCION:
        return this.getProyectoSgeAltaModoEjecucion();
      case PROYECTO_SGE_MODIFICACION_MODO_EJECUCION:
        return this.getProyectoSgeModificacionModoEjecucion();
      case CALENDARIO_FACTURACION_SGE_ENABLED:
        return this.getCalendarioFacturacionSgeEnabled();
      case FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES:
        return this.getFacturasGastosColumnasFijasVisibles();
      case VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES:
        return this.getViajesDietasColumnasFijasVisibles();
      case PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES:
        return this.getPersonalContratadoColumnasFijasVisibles();
      case PROYECTO_SOCIO_PAIS_FILTER_ENABLED:
        return this.getProyectoSocioPaisFilterEnabled();
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
      case VALIDACION_CLASIFICACION_GASTOS:
        this.setValidacionClasificacionGastos(ValidacionClasificacionGastos.valueOf(newValue));
        break;
      case EJECUCION_ECONOMICA_GRUPOS_ENABLED:
        this.setEjecucionEconomicaGruposEnabled(new Boolean(newValue));
        break;
      case CARDINALIDAD_RELACION_SGI_SGE:
        this.setCardinalidadRelacionSgiSge(CardinalidadRelacionSgiSge.valueOf(newValue));
        break;
      case PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED:
        this.setPartidasPresupuestariasSGE(new Boolean(newValue));
        break;
      case AMORTIZACION_FONDOS_SGE_ENABLED:
        this.setAmortizacionFondosSGE(new Boolean(newValue));
        break;
      case ALTA_BUSCADOR_SGE_ENABLED:
        this.setAltaBuscadorSGE(new Boolean(newValue));
        break;
      case GASTOS_JUSTIFICADOS_SGE_ENABLED:
        this.setGastosJustificadosSGE(new Boolean(newValue));
        break;
      case MODIFICACION_PROYECTO_SGE_ENABLED:
        this.setModificacionProyectoSge(new Boolean(newValue));
        break;
      case SECTOR_IVA_SGE_ENABLED:
        this.setSectorIvaSgeEnabled(new Boolean(newValue));
        break;
      case DETALLE_OPERACIONES_MODIFICACIONES_ENABLED:
        this.setDetalleOperacionesModificacionesEnabled(new Boolean(newValue));
        break;
      case PROYECTO_SGE_ALTA_MODO_EJECUCION:
        this.setProyectoSgeAltaModoEjecucion(ModoEjecucion.valueOf(newValue));
        break;
      case PROYECTO_SGE_MODIFICACION_MODO_EJECUCION:
        this.setProyectoSgeModificacionModoEjecucion(ModoEjecucion.valueOf(newValue));
        break;
      case CALENDARIO_FACTURACION_SGE_ENABLED:
        this.setCalendarioFacturacionSgeEnabled(new Boolean(newValue));
        break;
      case FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES:
        if (isValidEnumString(newValue, SgeFacturasJustificantesColumnasFijas.class, true)) {
          this.setFacturasGastosColumnasFijasVisibles(newValue);
        }
        break;
      case VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES:
        if (isValidEnumString(newValue, SgeFacturasJustificantesColumnasFijas.class, true)) {
          this.setViajesDietasColumnasFijasVisibles(newValue);
        }
        break;
      case PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES:
        if (isValidEnumString(newValue, SgeFacturasJustificantesColumnasFijas.class, true)) {
          this.setPersonalContratadoColumnasFijasVisibles(newValue);
        }
        break;
      case PROYECTO_SOCIO_PAIS_FILTER_ENABLED:
        this.setProyectoSocioPaisFilterEnabled(new Boolean(newValue));
        break;
    }
  }

  private static boolean isValidEnumString(String input, Class<? extends Enum<?>> enumClass, boolean allowNull) {
    if (StringUtils.isEmpty(input)) {
      return allowNull;
    }

    Set<String> validValues = Arrays.stream(enumClass.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.toSet());

    return Arrays.stream(input.split(","))
        .allMatch(validValues::contains);
  }
}