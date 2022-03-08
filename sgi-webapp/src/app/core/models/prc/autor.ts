import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IProduccionCientifica } from './produccion-cientifica';
import { IAutorGrupo } from './autor-grupo';

export interface IAutor {
  id: number;
  firma: string;
  persona: IPersona;
  nombre: string;
  apellidos: string;
  orden: number;
  orcidId: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  ip: boolean;
  produccionCientifica: IProduccionCientifica;
}

export interface IAutorWithGrupos {
  autor: IAutor;
  grupos: IAutorGrupo[];
}
