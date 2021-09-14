import { DateTime } from 'luxon';
import { IDocumento } from '../sgdoc/documento';

export interface IInvencionDocumento {
  id: number;
  fechaAnadido: DateTime;
  nombre: string;
  documento: IDocumento;
  invencionId: number;
}