export interface IGastoRequerimientoJustificacionRequest {
  gastoRef: string;
  requerimientoJustificacionId: number;
  importeAceptado: number;
  importeRechazado: number;
  importeAlegado: number;
  aceptado: boolean;
  incidencia: string;
  alegacion: string;
  identificadorJustificacion: string;
}
