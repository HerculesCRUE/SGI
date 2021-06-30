import { IChecklist } from '@core/models/eti/checklist';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IChecklistRequest } from './checklist-request';

class ChecklistRequestConverter extends SgiBaseConverter<IChecklistRequest, IChecklist>{
  toTarget(value: IChecklistRequest): IChecklist {
    if (!value) {
      return value as unknown as IChecklist;
    }
    return {
      id: undefined,
      persona: { id: value.personaRef } as IPersona,
      formly: undefined,
      respuesta: value.respuesta,
      fechaCreacion: undefined
    };
  }
  fromTarget(value: IChecklist): IChecklistRequest {
    if (!value) {
      return value as unknown as IChecklistRequest;
    }
    return {
      personaRef: value.persona?.id,
      formlyId: value.formly?.id,
      respuesta: value.respuesta
    };
  }
}

export const CHECKLIST_REQUEST_CONVERTER = new ChecklistRequestConverter();
