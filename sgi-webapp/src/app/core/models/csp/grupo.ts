import { DateTime } from 'luxon';
import { IProyectoSge } from '../sge/proyecto-sge';
import { Tipo } from './grupo-tipo';
import { ISolicitud } from './solicitud';

export interface IGrupo {
  id: number;
  nombre: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  proyectoSge: IProyectoSge;
  solicitud: ISolicitud;
  codigo: string;
  tipo: Tipo;
  especialInvestigacion: boolean;
  departamentoOrigenRef: string;
  activo: boolean;
}
