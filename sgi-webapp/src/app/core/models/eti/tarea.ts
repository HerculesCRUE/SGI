import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IEquipoTrabajo } from './equipo-trabajo';
import { IFormacionEspecifica } from './formacion-especifica';
import { IMemoria } from './memoria';
import { ITipoTarea } from './tipo-tarea';

export interface ITarea {
  /** Id */
  id: number;
  /** Equipo de trabajo. */
  equipoTrabajo: IEquipoTrabajo;
  /** Memoria */
  memoria: IMemoria;
  /** Formación específica */
  formacionEspecifica: IFormacionEspecifica;
  /** Nombre */
  nombre: I18nFieldValue[];
  /** Formación */
  formacion: I18nFieldValue[];
  /** Organismo */
  organismo: I18nFieldValue[];
  /** Año */
  anio: number;
  /** Tipo tarea */
  tipoTarea: ITipoTarea;
}
