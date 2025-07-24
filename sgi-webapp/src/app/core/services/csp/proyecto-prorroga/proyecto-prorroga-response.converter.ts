import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoProrrogaResponse } from './proyecto-prorroga-response';

class ProyectoProrrogaResponseConverter extends SgiBaseConverter<IProyectoProrrogaResponse, IProyectoProrroga> {

  toTarget(value: IProyectoProrrogaResponse): IProyectoProrroga {
    if (!value) {
      return value as unknown as IProyectoProrroga;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      numProrroga: value.numProrroga,
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      tipo: value.tipo,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      importe: value.importe,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }

  fromTarget(value: IProyectoProrroga): IProyectoProrrogaResponse {
    if (!value) {
      return value as unknown as IProyectoProrrogaResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      numProrroga: value.numProrroga,
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      tipo: value.tipo,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      importe: value.importe,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const PROYECTO_PRORROGA_RESPONSE_CONVERTER = new ProyectoProrrogaResponseConverter();
