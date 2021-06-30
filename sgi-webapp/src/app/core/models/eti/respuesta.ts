import { IApartado } from './apartado';
import { IMemoria } from './memoria';
import { ITipoDocumento } from './tipo-documento';

export interface IRespuesta {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** apartado */
  apartado: IApartado;
  /** respuestaDocumento */
  tipoDocumento: ITipoDocumento;
  /** valor */
  valor: {
    [name: string]: any;
  };
}
