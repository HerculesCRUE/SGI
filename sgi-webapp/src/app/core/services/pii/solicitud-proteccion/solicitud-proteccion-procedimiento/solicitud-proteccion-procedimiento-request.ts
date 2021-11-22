
export interface IProcedimientoRequest {

  fecha: string;
  tipoProcedimientoId: number;
  solicitudProteccionId: number;
  accionATomar: string;
  fechaLimiteAccion: string;
  generarAviso: boolean;
  comentarios: string;

}
