export interface IAlegacionRequerimientoResponse {
  id: number;
  requerimientoJustificacionId: number;
  fechaAlegacion: string;
  importeAlegado: number;
  importeAlegadoCd: number;
  importeAlegadoCi: number;
  importeReintegrado: number;
  importeReintegradoCd: number;
  importeReintegradoCi: number;
  interesesReintegrados: number;
  fechaReintegro: string;
  justificanteReintegro: string;
  observaciones: string;
}
