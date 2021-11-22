import { DateTime } from 'luxon';
import { IProyecto } from '../csp/proyecto';
import { ITipoProteccion } from './tipo-proteccion';

export interface IInvencion {
  id: number;
  titulo: string;
  fechaComunicacion: DateTime;
  descripcion: string;
  comentarios: string;
  proyecto: IProyecto;
  tipoProteccion: ITipoProteccion;
  activo: boolean;
}
