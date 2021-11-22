import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';

export interface IProcedimientoResponse {

  id: number;
  fecha: string;
  tipoProcedimiento: ITipoProcedimiento;
  solicitudProteccionId: number;
  accionATomar: string;
  fechaLimiteAccion: string;
  generarAviso: boolean;
  comentarios: string;

}
