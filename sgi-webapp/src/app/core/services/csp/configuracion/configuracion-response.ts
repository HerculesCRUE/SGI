export interface IConfiguracionResponse {
  id: number;
  formatoPartidaPresupuestaria: string;
  plantillaFormatoPartidaPresupuestaria: string;
  validacionClasificacionGastos: string;
  formatoIdentificadorJustificacion: string;
  plantillaFormatoIdentificadorJustificacion: string;
  dedicacionMinimaGrupo: number;
  formatoCodigoInternoProyecto: string;
  plantillaFormatoCodigoInternoProyecto: string;
  ejecucionEconomicaGruposEnabled: boolean;
  cardinalidadRelacionSgiSge: string;
  partidasPresupuestariasSgeEnabled: boolean;
  amortizacionFondosSgeEnabled: boolean;
  gastosJustificadosSgeEnabled: boolean;
  modificacionProyectoSgeEnabled: boolean;
  sectorIvaSgeEnabled: boolean;
  detalleOperacionesModificacionesEnabled: boolean;
  proyectoSgeAltaModoEjecucion: string;
  proyectoSgeModificacionModoEjecucion: string;
  calendarioFacturacionSgeEnabled: string;
  facturasGastosColumnasFijasVisibles: string;
  viajesDietasColumnasFijasVisibles: string;
  personalContratadoColumnasFijasVisibles: string;
}