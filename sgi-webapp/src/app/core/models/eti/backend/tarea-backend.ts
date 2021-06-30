import { FormacionEspecifica } from '../formacion-especifica';
import { TipoTarea } from '../tipo-tarea';
import { IEquipoTrabajoBackend } from './equipo-trabajo-backend';
import { IMemoriaBackend } from './memoria-backend';

export interface ITareaBackend {
  /** Id */
  id: number;
  /** Equipo de trabajo. */
  equipoTrabajo: IEquipoTrabajoBackend;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** Formación específica */
  formacionEspecifica: FormacionEspecifica;
  /** Tarea */
  tarea: string;
  /** Formación */
  formacion: string;
  /** Organismo */
  organismo: string;
  /** Año */
  anio: number;
  /** Tipo tarea */
  tipoTarea: TipoTarea;
}
