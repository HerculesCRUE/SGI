import { IDocumento } from '../sgdoc/documento';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IAcreditacion {
  id: number;
  documento: IDocumento;
  url: string;
  produccionCientifica: IProduccionCientifica;
}
