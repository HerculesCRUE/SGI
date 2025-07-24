import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGrupo } from '@core/models/csp/grupo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoResponse } from './grupo-response';

class GrupoResponseConverter
  extends SgiBaseConverter<IGrupoResponse, IGrupo> {
  toTarget(value: IGrupoResponse): IGrupo {
    if (!value) {
      return value as unknown as IGrupo;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      proyectoSge: value.proyectoSgeRef ? { id: value.proyectoSgeRef } as IProyectoSge : null,
      solicitud: value.solicitudId ? { id: value.solicitudId } as ISolicitud : null,
      codigo: value.codigo,
      tipo: value.tipo,
      especialInvestigacion: value.especialInvestigacion,
      departamentoOrigenRef: null,
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resumen) : [],
      activo: value.activo
    };
  }

  fromTarget(value: IGrupo): IGrupoResponse {
    if (!value) {
      return value as unknown as IGrupoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      proyectoSgeRef: value.proyectoSge?.id,
      solicitudId: value.solicitud?.id,
      codigo: value.codigo,
      tipo: value.tipo,
      especialInvestigacion: value.especialInvestigacion,
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resumen) : [],
      activo: value.activo
    };
  }
}

export const GRUPO_RESPONSE_CONVERTER = new GrupoResponseConverter();
