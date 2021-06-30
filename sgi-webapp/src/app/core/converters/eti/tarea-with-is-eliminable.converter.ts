import { ITareaWithIsEliminableBackend } from '@core/models/eti/backend/tarea-with-is-eliminable-backend';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EQUIPO_TRABAJO_CONVERTER } from './equipo-trabajo.converter';
import { MEMORIA_CONVERTER } from './memoria.converter';

class TareaWithIsEliminableConverter extends SgiBaseConverter<ITareaWithIsEliminableBackend, ITareaWithIsEliminable> {
  toTarget(value: ITareaWithIsEliminableBackend): ITareaWithIsEliminable {
    if (!value) {
      return value as unknown as ITareaWithIsEliminable;
    }
    return {
      id: value.id,
      equipoTrabajo: EQUIPO_TRABAJO_CONVERTER.toTarget(value.equipoTrabajo),
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      formacionEspecifica: value.formacionEspecifica,
      tarea: value.tarea,
      formacion: value.formacion,
      organismo: value.organismo,
      anio: value.anio,
      tipoTarea: value.tipoTarea,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: ITareaWithIsEliminable): ITareaWithIsEliminableBackend {
    if (!value) {
      return value as unknown as ITareaWithIsEliminableBackend;
    }
    return {
      id: value.id,
      equipoTrabajo: EQUIPO_TRABAJO_CONVERTER.fromTarget(value.equipoTrabajo),
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      formacionEspecifica: value.formacionEspecifica,
      tarea: value.tarea,
      formacion: value.formacion,
      organismo: value.organismo,
      anio: value.anio,
      tipoTarea: value.tipoTarea,
      eliminable: value.eliminable
    };
  }
}

export const TAREA_WITH_IS_ELIMINABLE_CONVERTER = new TareaWithIsEliminableConverter();
