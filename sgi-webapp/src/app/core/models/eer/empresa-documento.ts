import { IDocumento } from '../sgdoc/documento';
import { IEmpresaExplotacionResultados } from './empresa-explotacion-resultados';
import { ITipoDocumento } from './tipo-documento';

export interface IEmpresaDocumento {
  id: number;
  nombre: string;
  documento: IDocumento;
  comentarios: string;
  empresa: IEmpresaExplotacionResultados;
  tipoDocumento: ITipoDocumento;
  subtipoDocumento: ITipoDocumento;
}
