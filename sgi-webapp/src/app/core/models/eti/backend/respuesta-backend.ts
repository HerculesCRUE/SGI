import { IApartado } from '../apartado';
import { ITipoDocumento } from '../tipo-documento';
import { IMemoriaBackend } from './memoria-backend';

export interface IRespuestaBackend {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** apartado */
  apartado: IApartado;
  /** Tipo de documento */
  tipoDocumento: ITipoDocumento;
  /** valor */
  valor: {
    [name: string]: any;
  };
}
