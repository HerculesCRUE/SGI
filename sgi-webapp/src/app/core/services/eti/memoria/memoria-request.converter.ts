import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IMemoriaRequest } from './memoria-request';

class MemoriaRequestConverter extends SgiBaseConverter<IMemoriaRequest, IMemoria> {
  toTarget(value: IMemoriaRequest): IMemoria {
    if (!value) {
      return value as unknown as IMemoria;
    }
    return {
      id: null,
      numReferencia: null,
      peticionEvaluacion: { id: value.peticionEvaluacionId } as IPeticionEvaluacion,
      comite: { id: value.comiteId } as IComite,
      titulo: value.titulo ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.titulo) : [],
      responsable: { id: value.responsableRef } as IPersona,
      tipo: value.tipo,
      fechaEnvioSecretaria: null,
      requiereRetrospectiva: null,
      version: null,
      estadoActual: null,
      isResponsable: null,
      retrospectiva: null,
      memoriaOriginal: null,
      activo: null,
      formulario: null,
      formularioSeguimientoAnual: null,
      formularioSeguimientoFinal: null,
      formularioRetrospectiva: null
    };
  }

  fromTarget(value: IMemoria): IMemoriaRequest {
    if (!value) {
      return value as unknown as IMemoriaRequest;
    }
    return {
      peticionEvaluacionId: value.peticionEvaluacion?.id,
      comiteId: value.comite?.id,
      titulo: value.titulo ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.titulo) : [],
      responsableRef: value.responsable?.id,
      tipo: value.tipo
    };
  }
}

export const MEMORIA_REQUEST_CONVERTER = new MemoriaRequestConverter();
