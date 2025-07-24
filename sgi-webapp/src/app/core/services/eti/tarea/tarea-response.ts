import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IEquipoTrabajoResponse } from '../equipo-trabajo/equipo-trabajo-response';
import { IFormacionEspecificaResponse } from '../formacion-especifica/formacion-especifica-response';
import { IMemoriaResponse } from '../memoria/memoria-response';
import { ITipoTareaResponse } from '../tipo-tarea/tipo-tarea-response';

export interface ITareaResponse {
  /** Id */
  id: number;
  /** Equipo de trabajo. */
  equipoTrabajo: IEquipoTrabajoResponse;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** Formación específica */
  formacionEspecifica: IFormacionEspecificaResponse;
  /** Nombre */
  nombre: I18nFieldValueResponse[];
  /** Formación */
  formacion: I18nFieldValueResponse[];
  /** Organismo */
  organismo: I18nFieldValueResponse[];
  /** Año */
  anio: number;
  /** Tipo tarea */
  tipoTarea: ITipoTareaResponse;
}
