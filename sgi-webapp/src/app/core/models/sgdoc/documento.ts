import { DateTime } from 'luxon';

export interface IDocumento {
  /** ID */
  documentoRef: string;
  /** Nombre */
  nombre: string;
  /** Versión */
  version: number;
  /** Archivo */
  archivo: string;
  /** Fecha creación */
  fechaCreacion: DateTime;
  /** Tipo */
  tipo: string;

  autorRef: string;
}
