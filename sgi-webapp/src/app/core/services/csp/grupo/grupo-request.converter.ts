import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGrupo } from '@core/models/csp/grupo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoRequest } from './grupo-request';

class GrupoRequestConverter
  extends SgiBaseConverter<IGrupoRequest, IGrupo> {
  toTarget(value: IGrupoRequest): IGrupo {
    if (!value) {
      return value as unknown as IGrupo;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      proyectoSge: value.proyectoSgeRef ? { id: value.proyectoSgeRef } as IProyectoSge : undefined,
      solicitud: value.solicitudId ? { id: value.solicitudId } as ISolicitud : undefined,
      codigo: value.codigo,
      tipo: value.tipo,
      especialInvestigacion: value.especialInvestigacion,
      departamentoOrigenRef: value.departamentoOrigenRef,
      resumen: value.resumen ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.resumen) : [],
      activo: undefined
    };
  }

  fromTarget(value: IGrupo): IGrupoRequest {
    if (!value) {
      return value as unknown as IGrupoRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      proyectoSgeRef: value.proyectoSge?.id,
      solicitudId: value.solicitud?.id,
      codigo: value.codigo,
      tipo: value.tipo,
      especialInvestigacion: value.especialInvestigacion,
      resumen: value.resumen ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.resumen) : [],
      departamentoOrigenRef: value.departamentoOrigenRef
    };
  }
}

export const GRUPO_REQUEST_CONVERTER = new GrupoRequestConverter();
