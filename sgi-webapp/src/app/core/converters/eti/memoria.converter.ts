import { IMemoriaBackend } from '@core/models/eti/backend/memoria-backend';
import { IMemoria } from '@core/models/eti/memoria';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PETICION_EVALUACION_CONVERTER } from './peticion-evaluacion.converter';
import { RETROSPECTIVA_CONVERTER } from './retrospectiva.converter';

class MemoriaConverter extends SgiBaseConverter<IMemoriaBackend, IMemoria> {
  toTarget(value: IMemoriaBackend): IMemoria {
    if (!value) {
      return value as unknown as IMemoria;
    }
    return {
      id: value.id,
      numReferencia: value.numReferencia,
      peticionEvaluacion: PETICION_EVALUACION_CONVERTER.toTarget(value.peticionEvaluacion),
      comite: value.comite,
      titulo: value.titulo,
      responsable: { id: value.personaRef } as IPersona,
      tipoMemoria: value.tipoMemoria,
      fechaEnvioSecretaria: LuxonUtils.fromBackend(value.fechaEnvioSecretaria),
      requiereRetrospectiva: value.requiereRetrospectiva,
      version: value.version,
      estadoActual: value.estadoActual,
      isResponsable: value.isResponsable,
      retrospectiva: RETROSPECTIVA_CONVERTER.toTarget(value.retrospectiva),
      codOrganoCompetente: value.codOrganoCompetente,
      memoriaOriginal: MEMORIA_CONVERTER.toTarget(value.memoriaOriginal),
      activo: value.activo
    };
  }

  fromTarget(value: IMemoria): IMemoriaBackend {
    if (!value) {
      return value as unknown as IMemoriaBackend;
    }
    return {
      id: value.id,
      numReferencia: value.numReferencia,
      peticionEvaluacion: PETICION_EVALUACION_CONVERTER.fromTarget(value.peticionEvaluacion),
      comite: value.comite,
      titulo: value.titulo,
      personaRef: value.responsable?.id,
      tipoMemoria: value.tipoMemoria,
      fechaEnvioSecretaria: LuxonUtils.toBackend(value.fechaEnvioSecretaria),
      requiereRetrospectiva: value.requiereRetrospectiva,
      version: value.version,
      estadoActual: value.estadoActual,
      isResponsable: value.isResponsable,
      retrospectiva: RETROSPECTIVA_CONVERTER.fromTarget(value.retrospectiva),
      codOrganoCompetente: value.codOrganoCompetente,
      memoriaOriginal: MEMORIA_CONVERTER.fromTarget(value.memoriaOriginal),
      activo: value.activo
    };
  }
}

export const MEMORIA_CONVERTER = new MemoriaConverter();
