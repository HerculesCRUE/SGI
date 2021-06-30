import { IEquipoTrabajo } from './equipo-trabajo';
import { FormacionEspecifica } from './formacion-especifica';
import { IMemoria } from './memoria';
import { TipoTarea } from './tipo-tarea';

export interface ITarea {
  /** Id */
  id: number;
  /** Equipo de trabajo. */
  equipoTrabajo: IEquipoTrabajo;
  /** Memoria */
  memoria: IMemoria;
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
