import { ITipoDocumento } from './tipo-documento';

export interface IRespuesta {
  /** ID */
  id: number;
  /** Memoria */
  memoriaId: number;
  /** apartado */
  apartadoId: number;
  /** respuestaDocumento */
  tipoDocumento: ITipoDocumento;
  /** valor */
  valor: {
    [name: string]: any;
  };
}
