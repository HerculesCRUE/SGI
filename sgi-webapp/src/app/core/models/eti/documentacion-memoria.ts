import { IDocumento } from '../sgdoc/documento';
import { IMemoria } from './memoria';
import { ITipoDocumento } from './tipo-documento';

export interface IDocumentacionMemoria {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** TIpo de documento */
  tipoDocumento: ITipoDocumento;
  nombre: string;
  /** Ref del documento */
  documento: IDocumento;
}
