import { marker } from "@biesbjerg/ngx-translate-extract-marker";

export enum ValidacionClasificacionGastos {
  VALIDACION = 'VALIDACION',
  CLASIFICACION = 'CLASIFICACION',
  ELEGIBILIDAD = 'ELEGIBILIDAD'
}

export enum CalendarioFacturacionSgeIntegration {
  INTEGRACION_LECTURA_ESCRITURA = "INTEGRACION_LECTURA_ESCRITURA",
  INTEGRACION_SOLO_LECTURA = "INTEGRACION_SOLO_LECTURA",
  SIN_INTEGRACION = "SIN_INTEGRACION"
}

export enum CardinalidadRelacionSgiSge {
  SGI_1_SGE_1 = 'SGI_1_SGE_1',
  SGI_1_SGE_N = 'SGI_1_SGE_N',
  SGI_N_SGE_1 = 'SGI_N_SGE_1',
  SGI_N_SGE_N = 'SGI_N_SGE_N'
}

export enum ModoEjecucion {
  ASINCRONA = 'ASINCRONA',
  SINCRONA = 'SINCRONA'
}

export enum FacturasJustificantesColumnasFijasConfigurables {
  CLASIFICACION_SGE = 'CLASIFICACION_SGE',
  APLICACION_PRESUPUESTARIA = 'APLICACION_PRESUPUESTARIA'
}

export enum SgeFiltroAnualidades {
  ANUALIDADES_OPCIONALES = 'ANUALIDADES_OPCIONALES',
  ANUALIDADES_OBLIGATORIAS = 'ANUALIDADES_OBLIGATORIAS'
}

export const CALENDARIO_FACTURACION_SGE_INTEGRATION_MAP: Map<CalendarioFacturacionSgeIntegration, string> = new Map([
  [CalendarioFacturacionSgeIntegration.INTEGRACION_LECTURA_ESCRITURA, marker(`csp.calendario-facturacion-sge-integration.INTEGRACION_LECTURA_ESCRITURA`)],
  [CalendarioFacturacionSgeIntegration.INTEGRACION_SOLO_LECTURA, marker(`csp.calendario-facturacion-sge-integration.INTEGRACION_SOLO_LECTURA`)],
  [CalendarioFacturacionSgeIntegration.SIN_INTEGRACION, marker(`csp.calendario-facturacion-sge-integration.SIN_INTEGRACION`)]
]);

export const CARDINALIDAD_RELACION_SGI_SGE_MAP: Map<CardinalidadRelacionSgiSge, string> = new Map([
  [CardinalidadRelacionSgiSge.SGI_1_SGE_1, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_1_SGE_1`)],
  [CardinalidadRelacionSgiSge.SGI_1_SGE_N, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_1_SGE_N`)],
  [CardinalidadRelacionSgiSge.SGI_N_SGE_1, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_N_SGE_1`)],
  [CardinalidadRelacionSgiSge.SGI_N_SGE_N, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_N_SGE_N`)]
]);

export const MODO_EJECUCION_MAP: Map<ModoEjecucion, string> = new Map([
  [ModoEjecucion.ASINCRONA, marker(`csp.modo-ejecucion.ASINCRONA`)],
  [ModoEjecucion.SINCRONA, marker(`csp.modo-ejecucion.SINCRONA`)]
]);

export const VALIDACION_CLASIFICACION_GASTOS_MAP: Map<ValidacionClasificacionGastos, string> = new Map([
  [ValidacionClasificacionGastos.VALIDACION, marker(`csp.validacion-clasificacion-gastos.VALIDACION`)],
  [ValidacionClasificacionGastos.CLASIFICACION, marker(`csp.validacion-clasificacion-gastos.CLASIFICACION`)],
  [ValidacionClasificacionGastos.ELEGIBILIDAD, marker(`csp.validacion-clasificacion-gastos.ELEGIBILIDAD`)]
]);

export const FACTURAS_JUSTIFICANTES_COLUMNAS_FIJAS_CONFIGURABLES_MAP: Map<FacturasJustificantesColumnasFijasConfigurables, string> = new Map([
  [FacturasJustificantesColumnasFijasConfigurables.CLASIFICACION_SGE, marker(`csp.facturas-justificantes-columnas-fijas-configurables.CLASIFICACION_SGE`)],
  [FacturasJustificantesColumnasFijasConfigurables.APLICACION_PRESUPUESTARIA, marker(`csp.facturas-justificantes-columnas-fijas-configurables.APLICACION_PRESUPUESTARIA`)]
]);

export const SGE_FILTRO_ANUALIDADES_MAP: Map<SgeFiltroAnualidades, string> = new Map([
  [SgeFiltroAnualidades.ANUALIDADES_OPCIONALES, marker(`csp.sge-filtro-anualidades.ANUALIDADES_OPCIONALES`)],
  [SgeFiltroAnualidades.ANUALIDADES_OBLIGATORIAS, marker(`csp.sge-filtro-anualidades.ANUALIDADES_OBLIGATORIAS`)]
]);

export enum SgeIntegracionesEccMenus {
  ECC_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL = 'ECC_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL',
  ECC_EJECUCION_PRESUPUESTARIA_GASTOS = 'ECC_EJECUCION_PRESUPUESTARIA_GASTOS',
  ECC_EJECUCION_PRESUPUESTARIA_INGRESOS = 'ECC_EJECUCION_PRESUPUESTARIA_INGRESOS',
  ECC_DETALLE_OPERACIONES_GASTOS = 'ECC_DETALLE_OPERACIONES_GASTOS',
  ECC_DETALLE_OPERACIONES_INGRESOS = 'ECC_DETALLE_OPERACIONES_INGRESOS',
  ECC_DETALLE_OPERACIONES_MODIFICACIONES = 'ECC_DETALLE_OPERACIONES_MODIFICACIONES',
  ECC_FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS = 'ECC_FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS',
  ECC_FACTURAS_JUSTIFICANTES_VIAJES_DIETAS = 'ECC_FACTURAS_JUSTIFICANTES_VIAJES_DIETAS',
  ECC_FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO = 'ECC_FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO',
  ECC_FACTURAS_EMITIDAS = 'ECC_FACTURAS_EMITIDAS',
  ECC_SEGUIMIENTO_JUSTIFICACION_RESUMEN = 'ECC_SEGUIMIENTO_JUSTIFICACION_RESUMEN',
  ECC_SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS = 'ECC_SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS'
}

export const SGE_INTEGRACIONES_ECC_MENUS_MAP: Map<SgeIntegracionesEccMenus, string> = new Map([
  [SgeIntegracionesEccMenus.ECC_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, marker(`csp.sge-integraciones-ecc-menus.ECC_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL`)],
  [SgeIntegracionesEccMenus.ECC_EJECUCION_PRESUPUESTARIA_GASTOS, marker(`csp.sge-integraciones-ecc-menus.ECC_EJECUCION_PRESUPUESTARIA_GASTOS`)],
  [SgeIntegracionesEccMenus.ECC_EJECUCION_PRESUPUESTARIA_INGRESOS, marker(`csp.sge-integraciones-ecc-menus.ECC_EJECUCION_PRESUPUESTARIA_INGRESOS`)],
  [SgeIntegracionesEccMenus.ECC_DETALLE_OPERACIONES_GASTOS, marker(`csp.sge-integraciones-ecc-menus.ECC_DETALLE_OPERACIONES_GASTOS`)],
  [SgeIntegracionesEccMenus.ECC_DETALLE_OPERACIONES_INGRESOS, marker(`csp.sge-integraciones-ecc-menus.ECC_DETALLE_OPERACIONES_INGRESOS`)],
  [SgeIntegracionesEccMenus.ECC_DETALLE_OPERACIONES_MODIFICACIONES, marker(`csp.sge-integraciones-ecc-menus.ECC_DETALLE_OPERACIONES_MODIFICACIONES`)],
  [SgeIntegracionesEccMenus.ECC_FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS, marker(`csp.sge-integraciones-ecc-menus.ECC_FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS`)],
  [SgeIntegracionesEccMenus.ECC_FACTURAS_JUSTIFICANTES_VIAJES_DIETAS, marker(`csp.sge-integraciones-ecc-menus.ECC_FACTURAS_JUSTIFICANTES_VIAJES_DIETAS`)],
  [SgeIntegracionesEccMenus.ECC_FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO, marker(`csp.sge-integraciones-ecc-menus.ECC_FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO`)],
  [SgeIntegracionesEccMenus.ECC_FACTURAS_EMITIDAS, marker(`csp.sge-integraciones-ecc-menus.ECC_FACTURAS_EMITIDAS`)],
  [SgeIntegracionesEccMenus.ECC_SEGUIMIENTO_JUSTIFICACION_RESUMEN, marker(`csp.sge-integraciones-ecc-menus.ECC_SEGUIMIENTO_JUSTIFICACION_RESUMEN`)],
  [SgeIntegracionesEccMenus.ECC_SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS, marker(`csp.sge-integraciones-ecc-menus.ECC_SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS`)]
]);

export enum SgeEjecucionEconomicaFiltros {
  ECC_FILTRO_FACTURAS_GASTOS_FECHA_DEVENGO = 'ECC_FILTRO_FACTURAS_GASTOS_FECHA_DEVENGO',
  ECC_FILTRO_FACTURAS_GASTOS_FECHA_CONTABILIZACION = 'ECC_FILTRO_FACTURAS_GASTOS_FECHA_CONTABILIZACION',
  ECC_FILTRO_FACTURAS_GASTOS_FECHA_PAGO = 'ECC_FILTRO_FACTURAS_GASTOS_FECHA_PAGO',
  ECC_FILTRO_VIAJES_DIETAS_FECHA_DEVENGO = 'ECC_FILTRO_VIAJES_DIETAS_FECHA_DEVENGO',
  ECC_FILTRO_VIAJES_DIETAS_FECHA_CONTABILIZACION = 'ECC_FILTRO_VIAJES_DIETAS_FECHA_CONTABILIZACION',
  ECC_FILTRO_VIAJES_DIETAS_FECHA_PAGO = 'ECC_FILTRO_VIAJES_DIETAS_FECHA_PAGO',
  ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_DEVENGO = 'ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_DEVENGO',
  ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_CONTABILIZACION = 'ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_CONTABILIZACION',
  ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_PAGO = 'ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_PAGO',
  ECC_FILTRO_FACTURAS_EMITIDAS_FECHA_FACTURA = 'ECC_FILTRO_FACTURAS_EMITIDAS_FECHA_FACTURA'
}

export const SGE_EJECUCION_ECONOMICA_FILTROS_MAP: Map<SgeEjecucionEconomicaFiltros, string> = new Map([
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_FACTURAS_GASTOS_FECHA_DEVENGO, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_FACTURAS_GASTOS_FECHA_DEVENGO`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_FACTURAS_GASTOS_FECHA_CONTABILIZACION, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_FACTURAS_GASTOS_FECHA_CONTABILIZACION`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_FACTURAS_GASTOS_FECHA_PAGO, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_FACTURAS_GASTOS_FECHA_PAGO`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_VIAJES_DIETAS_FECHA_DEVENGO, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_VIAJES_DIETAS_FECHA_DEVENGO`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_VIAJES_DIETAS_FECHA_CONTABILIZACION, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_VIAJES_DIETAS_FECHA_CONTABILIZACION`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_VIAJES_DIETAS_FECHA_PAGO, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_VIAJES_DIETAS_FECHA_PAGO`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_DEVENGO, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_DEVENGO`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_CONTABILIZACION, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_CONTABILIZACION`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_PAGO, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_PERSONAL_CONTRATADO_FECHA_PAGO`)],
  [SgeEjecucionEconomicaFiltros.ECC_FILTRO_FACTURAS_EMITIDAS_FECHA_FACTURA, marker(`csp.sge-ejecucion-economica-filtros.ECC_FILTRO_FACTURAS_EMITIDAS_FECHA_FACTURA`)]
]);


export interface IConfiguracion {
  id: number;
  /** Habilitar que se muestre el buscador de proyectos económicos al pulsar el botón de "Añadir identificador SGE" en la pantalla de  "Configuración económica - Identificación" */
  altaBuscadorSgeEnabled: boolean;
  /** Expresión regular para validar el formato del código de las partidas presupuestarias */
  formatoPartidaPresupuestaria: string;
  /** Plantilla informativa del formato del código de las partidas presupuestarias */
  plantillaFormatoPartidaPresupuestaria: string;
  /** Determina cuándo la validación de gastos está activa en la app */
  validacionClasificacionGastos: ValidacionClasificacionGastos;
  /** Expresión regular para validar el formato del código de los identificadores de justificación */
  formatoIdentificadorJustificacion: string;
  /** Plantilla informativa del formato del código de los identificadores de justificación */
  plantillaFormatoIdentificadorJustificacion: string;
  /** Dedicacion minima de un miembro de un grupo de investigacion */
  dedicacionMinimaGrupo: number;
  /** Expresión regular para validar el formato del código interno de proyecto */
  formatoCodigoInternoProyecto: string;
  /** Plantilla informativa del formato del código interno de proyecto */
  plantillaFormatoCodigoInternoProyecto: string;
  /** Habilitar Ejecución económica de Grupos de investigación */
  ejecucionEconomicaGruposEnabled: boolean;
  /** Cardinalidad relación proyecto SGI - identificador SGE */
  cardinalidadRelacionSgiSge: CardinalidadRelacionSgiSge;
  /** Habilitar creación de Partidas presupuestarias en el SGE */
  partidasPresupuestariasSgeEnabled: boolean;
  /** Habilitar creación de Periodos de amortización en el SGE */
  amortizacionFondosSgeEnabled: boolean;
  /** Habilitar la integración de gastos justificados (apartado seguimiento de justificación) SGE */
  gastosJustificadosSgeEnabled: boolean;
  /** Habilitar la acción de solicitar modificación de los datos del proyecto SGE */
  modificacionProyectoSgeEnabled: boolean;
  /** Habilitar la visualización del campo Sector IVA proveniente de la integración con el SGE */
  sectorIvaSgeEnabled: boolean;
  /** Habilitar la visualización de las opciones del menú de ejecución económica */
  integracionesEccSgeEnabled: SgeIntegracionesEccMenus[];
  /** Determina si el alta del proyecto económico en el SGE se realiza de forma sincrona o de forma asíncrona */
  proyectoSgeAltaModoEjecucion: ModoEjecucion;
  /** Determina si la modificacion del proyecto económico en el SGE se realiza de forma sincrona o de forma asíncrona */
  proyectoSgeModificacionModoEjecucion: ModoEjecucion;
  /** Determina si hay integración del calendario facturación con el SGE para indicar si se van a notificar las facturas previstas validadas del calendario de facturación al SGE */
  calendarioFacturacionSgeIntegration: CalendarioFacturacionSgeIntegration;
  /** Columnas a mostrar en Facturas y gastos (ejecución económica - facturas y justificantes) *//** Columnas a mostrar en Personal Contratado (ejecución económica - facturas y justificantes) */
  facturasGastosColumnasFijasVisibles: FacturasJustificantesColumnasFijasConfigurables[];
  /** Columnas a mostrar en Viajes y dietas (ejecución económica - facturas y justificantes) */
  viajesDietasColumnasFijasVisibles: FacturasJustificantesColumnasFijasConfigurables[];
  /** Columnas a mostrar en Personal Contratado (ejecución económica - facturas y justificantes) */
  personalContratadoColumnasFijasVisibles: FacturasJustificantesColumnasFijasConfigurables[];
  /** Habilitar el filtro de busqueda por pais de socio en el listado de proyectos */
  proyectoSocioPaisFilterEnabled: boolean;
  /** Permitir creación de solicitudes sin convocatoria desde perfil de investigación */
  solicitudesSinConvocatoriaInvestigadorEnabled: boolean;
  /** Integración de notificación de presupuestos al SGE */
  notificacionPresupuestoSgeEnabled: boolean;
  /** Permite activar formato de año natural en anualidad de presupuesto de proyecto */
  formatoAnualidadAnio: boolean;
  /** Define el comportamiento del filtro de anualidades en Ejecución Económica. */
  sgeFiltroAnualidades: SgeFiltroAnualidades;
  /** Habilita el modal para el detalle en ejecuciñon presupuestaria - Gasto */
  sgeEjecucionPresupuestariaGastosDetalleEnabled: boolean;
  /** Habilita el modal para el detalle en detalle operaciones - Gastos */
  sgeDetalleOperacionesGastosDetalleEnabled: boolean;
  /** Habilita los buscadores de las pantallas de Ejecución Económica dependientes del SGE. */
  sgeEjecucionEconomicaFiltros: SgeEjecucionEconomicaFiltros[];
  /** Habilita la acción eliminar relación proyecto SGI - identificador SGE */
  sgeEliminarRelacionProyectoEnabled: boolean;
}
