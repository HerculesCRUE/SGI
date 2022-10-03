export interface IGastoRequerimientoJustificacionResponse {
  id: number;
  gastoRef: string;
  importeAceptado: number;
  importeRechazado: number;
  importeAlegado: number;
  aceptado: boolean;
  incidencia: string;
  alegacion: string;
  identificadorJustificacion: string;
  requerimientoJustificacionId: number;
}
