import { IDocumento } from '../sgdoc/documento';
import { IProcedimiento } from './procedimiento';

export interface IProcedimientoDocumento {

  id: number;
  nombre: string;
  documento: IDocumento;
  procedimiento: IProcedimiento;

}
