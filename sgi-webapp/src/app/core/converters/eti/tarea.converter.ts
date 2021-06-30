import { ITareaBackend } from '@core/models/eti/backend/tarea-backend';
import { ITarea } from '@core/models/eti/tarea';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EQUIPO_TRABAJO_CONVERTER } from './equipo-trabajo.converter';
import { MEMORIA_CONVERTER } from './memoria.converter';

class TareaConverter extends SgiBaseConverter<ITareaBackend, ITarea> {
  toTarget(value: ITareaBackend): ITarea {
    if (!value) {
      return value as unknown as ITarea;
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
      tipoTarea: value.tipoTarea
    };
  }

  fromTarget(value: ITarea): ITareaBackend {
    if (!value) {
      return value as unknown as ITareaBackend;
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
      tipoTarea: value.tipoTarea
    };
  }
}

export const TAREA_CONVERTER = new TareaConverter();
