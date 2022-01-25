import { IDocumento } from '../sgdoc/documento';
import { IAutorizacion } from './autorizacion';

export interface ICertificadoAutorizacion {
  id: number;
  autorizacion: IAutorizacion;
  documento: IDocumento;
  nombre: string;
  visible: boolean;
}
