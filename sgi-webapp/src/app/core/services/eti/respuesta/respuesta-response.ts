import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IRespuestaResponse {
  /** ID */
  id: number;
  /** Memoria */
  memoriaId: number;
  /** apartado */
  apartadoId: number;
  /** Tipo de documento */
  tipoDocumento: ITipoDocumentoResponse;
  /** valor */
  valor: {
    [name: string]: any;
  };
}
