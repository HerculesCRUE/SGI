import { IChecklist } from '@core/models/eti/checklist';
import { IFormly } from '@core/models/eti/formly';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IChecklistResponse } from './checklist-response';

class ChecklistResponseConverter extends SgiBaseConverter<IChecklistResponse, IChecklist>{
  toTarget(value: IChecklistResponse): IChecklist {
    if (!value) {
      return value as unknown as IChecklist;
    }
    return {
      id: value.id,
      persona: { id: value.personaRef } as IPersona,
      formly: value.formly as IFormly,
      respuesta: value.respuesta,
      fechaCreacion: LuxonUtils.fromBackend(value.fechaCreacion)
    };
  }
  fromTarget(value: IChecklist): IChecklistResponse {
    if (!value) {
      return value as unknown as IChecklistResponse;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      formly: {
        id: value.formly?.id,
        esquema: value.formly?.esquema
      },
      fechaCreacion: LuxonUtils.toBackend(value.fechaCreacion),
      respuesta: value.respuesta
    };
  }
}

export const CHECKLIST_RESPONSE_CONVERTER = new ChecklistResponseConverter();
