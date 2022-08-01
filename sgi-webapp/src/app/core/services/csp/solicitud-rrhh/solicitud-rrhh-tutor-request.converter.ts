import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhTutorRequest } from './solicitud-rrhh-tutor-request';

class SolicitudRrhhTutorRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhTutorRequest, ISolicitudRrhhTutor> {
  toTarget(value: ISolicitudRrhhTutorRequest): ISolicitudRrhhTutor {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutor;
    }
    return {
      tutor: { id: value.tutorRef } as IPersona
    };
  }

  fromTarget(value: ISolicitudRrhhTutor): ISolicitudRrhhTutorRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutorRequest;
    }
    return {
      tutorRef: value.tutor.id
    };
  }
}

export const SOLICITUD_RRHH_TUTOR_REQUEST_CONVERTER = new SolicitudRrhhTutorRequestConverter();
