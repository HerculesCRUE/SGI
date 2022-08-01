import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhTutorResponse } from './solicitud-rrhh-tutor-response';

class SolicitudRrhhTutorResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhTutorResponse, ISolicitudRrhhTutor> {
  toTarget(value: ISolicitudRrhhTutorResponse): ISolicitudRrhhTutor {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutor;
    }
    return {
      tutor: value.tutorRef ? { id: value.tutorRef } as IPersona : null
    };
  }

  fromTarget(value: ISolicitudRrhhTutor): ISolicitudRrhhTutorResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutorResponse;
    }
    return {
      tutorRef: value.tutor?.id ?? null
    };
  }
}

export const SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER = new SolicitudRrhhTutorResponseConverter();
