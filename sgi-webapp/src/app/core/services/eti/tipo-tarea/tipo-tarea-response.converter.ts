import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoTarea } from '@core/models/eti/tipo-tarea';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoTareaResponse } from './tipo-tarea-response';

class TipoTareaResponseConverter extends SgiBaseConverter<ITipoTareaResponse, ITipoTarea> {
  toTarget(value: ITipoTareaResponse): ITipoTarea {
    if (!value) {
      return value as unknown as ITipoTarea;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }

  fromTarget(value: ITipoTarea): ITipoTareaResponse {
    if (!value) {
      return value as unknown as ITipoTareaResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const TIPO_TAREA_RESPONSE_CONVERTER = new TipoTareaResponseConverter();
