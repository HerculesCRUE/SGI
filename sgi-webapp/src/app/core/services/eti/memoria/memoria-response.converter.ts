import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IMemoria } from '@core/models/eti/memoria';
import { IPersona } from '@core/models/sgp/persona';
import { IMemoriaResponse } from '@core/services/eti/memoria/memoria-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { COMITE_RESPONSE_CONVERTER } from '../comite/comite-response.converter';
import { FORMULARIO_RESPONSE_CONVERTER } from '../formulario/formulario-response.converter';
import { PETICION_EVALUACION_RESPONSE_CONVERTER } from '../peticion-evaluacion/peticion-evaluacion-response.converter';
import { RETROSPECTIVA_RESPONSE_CONVERTER } from '../retrospectiva/retrospectiva-response.converter';

class MemoriaResponseConverter extends SgiBaseConverter<IMemoriaResponse, IMemoria> {
  toTarget(value: IMemoriaResponse): IMemoria {
    if (!value) {
      return value as unknown as IMemoria;
    }
    return {
      id: value.id,
      numReferencia: value.numReferencia,
      peticionEvaluacion: PETICION_EVALUACION_RESPONSE_CONVERTER.toTarget(value.peticionEvaluacion),
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      formulario: FORMULARIO_RESPONSE_CONVERTER.toTarget(value.formulario),
      formularioSeguimientoAnual: FORMULARIO_RESPONSE_CONVERTER.toTarget(value.formularioSeguimientoAnual),
      formularioSeguimientoFinal: FORMULARIO_RESPONSE_CONVERTER.toTarget(value.formularioSeguimientoFinal),
      formularioRetrospectiva: FORMULARIO_RESPONSE_CONVERTER.toTarget(value.formularioRetrospectiva),
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      responsable: { id: value.personaRef } as IPersona,
      tipo: value.tipo,
      fechaEnvioSecretaria: LuxonUtils.fromBackend(value.fechaEnvioSecretaria),
      requiereRetrospectiva: value.requiereRetrospectiva,
      version: value.version,
      estadoActual: value.estadoActual,
      isResponsable: value.isResponsable,
      retrospectiva: RETROSPECTIVA_RESPONSE_CONVERTER.toTarget(value.retrospectiva),
      memoriaOriginal: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoriaOriginal),
      activo: value.activo
    };
  }

  fromTarget(value: IMemoria): IMemoriaResponse {
    if (!value) {
      return value as unknown as IMemoriaResponse;
    }
    return {
      id: value.id,
      numReferencia: value.numReferencia,
      peticionEvaluacion: PETICION_EVALUACION_RESPONSE_CONVERTER.fromTarget(value.peticionEvaluacion),
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      formulario: FORMULARIO_RESPONSE_CONVERTER.fromTarget(value.formulario),
      formularioSeguimientoAnual: FORMULARIO_RESPONSE_CONVERTER.fromTarget(value.formularioSeguimientoAnual),
      formularioSeguimientoFinal: FORMULARIO_RESPONSE_CONVERTER.fromTarget(value.formularioSeguimientoFinal),
      formularioRetrospectiva: FORMULARIO_RESPONSE_CONVERTER.fromTarget(value.formularioRetrospectiva),
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      personaRef: value.responsable?.id,
      tipo: value.tipo,
      fechaEnvioSecretaria: LuxonUtils.toBackend(value.fechaEnvioSecretaria),
      requiereRetrospectiva: value.requiereRetrospectiva,
      version: value.version,
      estadoActual: value.estadoActual,
      isResponsable: value.isResponsable,
      retrospectiva: RETROSPECTIVA_RESPONSE_CONVERTER.fromTarget(value.retrospectiva),
      memoriaOriginal: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoriaOriginal),
      activo: value.activo
    };
  }
}

export const MEMORIA_RESPONSE_CONVERTER = new MemoriaResponseConverter();
