import { DateTime } from 'luxon';

export interface IPartidaPresupuestariaSge {
  id: string;
  codigo: string;
  descripcion: string,
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
