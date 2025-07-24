import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaExplotacionResultadosRequest } from './empresa-explotacion-resultados-request';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';

class EmpresaExplotacionResultadosRequestConverter
  extends SgiBaseConverter<IEmpresaExplotacionResultadosRequest, IEmpresaExplotacionResultados> {
  toTarget(value: IEmpresaExplotacionResultadosRequest): IEmpresaExplotacionResultados {
    if (!value) {
      return value as unknown as IEmpresaExplotacionResultados;
    }
    return {
      id: undefined,
      fechaSolicitud: LuxonUtils.fromBackend(value.fechaSolicitud),
      tipoEmpresa: value.tipoEmpresa,
      conocimientoTecnologia: value.conocimientoTecnologia ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.conocimientoTecnologia) : [],
      entidad: value.entidadRef ? { id: value.entidadRef } as IEmpresa : undefined,
      estado: value.estado,
      fechaAprobacionCG: LuxonUtils.fromBackend(value.fechaAprobacionCG),
      fechaCese: LuxonUtils.fromBackend(value.fechaCese),
      fechaConstitucion: LuxonUtils.fromBackend(value.fechaConstitucion),
      fechaDesvinculacion: LuxonUtils.fromBackend(value.fechaDesvinculacion),
      fechaIncorporacion: LuxonUtils.fromBackend(value.fechaIncorporacion),
      nombreRazonSocial: value.nombreRazonSocial ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombreRazonSocial) : [],
      notario: value.notario ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.notario) : [],
      numeroProtocolo: value.numeroProtocolo,
      objetoSocial: value.objetoSocial ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.objetoSocial) : [],
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.observaciones) : [],
      solicitante: value.solicitanteRef ? { id: value.solicitanteRef } as IPersona : undefined,
      activo: undefined
    };
  }

  fromTarget(value: IEmpresaExplotacionResultados): IEmpresaExplotacionResultadosRequest {
    if (!value) {
      return value as unknown as IEmpresaExplotacionResultadosRequest;
    }
    return {
      fechaSolicitud: LuxonUtils.toBackend(value.fechaSolicitud),
      tipoEmpresa: value.tipoEmpresa,
      conocimientoTecnologia: value.conocimientoTecnologia ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.conocimientoTecnologia) : [],
      entidadRef: value.entidad?.id,
      estado: value.estado,
      fechaAprobacionCG: LuxonUtils.toBackend(value.fechaAprobacionCG),
      fechaCese: LuxonUtils.toBackend(value.fechaCese),
      fechaConstitucion: LuxonUtils.toBackend(value.fechaConstitucion),
      fechaDesvinculacion: LuxonUtils.toBackend(value.fechaDesvinculacion),
      fechaIncorporacion: LuxonUtils.toBackend(value.fechaIncorporacion),
      nombreRazonSocial: value.nombreRazonSocial ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombreRazonSocial) : [],
      notario: value.notario ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.notario) : [],
      numeroProtocolo: value.numeroProtocolo,
      objetoSocial: value.objetoSocial ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.objetoSocial) : [],
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.observaciones) : [],
      solicitanteRef: value.solicitante?.id
    };
  }
}

export const EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER = new EmpresaExplotacionResultadosRequestConverter();
