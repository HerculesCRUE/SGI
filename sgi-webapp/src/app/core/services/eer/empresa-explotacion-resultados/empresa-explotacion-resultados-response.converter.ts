import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaExplotacionResultadosResponse } from './empresa-explotacion-resultados-response';

class EmpresaExplotacionResultadosResponseConverter
  extends SgiBaseConverter<IEmpresaExplotacionResultadosResponse, IEmpresaExplotacionResultados> {
  toTarget(value: IEmpresaExplotacionResultadosResponse): IEmpresaExplotacionResultados {
    if (!value) {
      return value as unknown as IEmpresaExplotacionResultados;
    }
    return {
      id: value.id,
      fechaSolicitud: LuxonUtils.fromBackend(value.fechaSolicitud),
      tipoEmpresa: value.tipoEmpresa,
      conocimientoTecnologia: value.conocimientoTecnologia,
      entidad: value.entidadRef ? { id: value.entidadRef } as IEmpresa : undefined,
      estado: value.estado,
      fechaAprobacionCG: LuxonUtils.fromBackend(value.fechaAprobacionCG),
      fechaCese: LuxonUtils.fromBackend(value.fechaCese),
      fechaConstitucion: LuxonUtils.fromBackend(value.fechaConstitucion),
      fechaDesvinculacion: LuxonUtils.fromBackend(value.fechaDesvinculacion),
      fechaIncorporacion: LuxonUtils.fromBackend(value.fechaIncorporacion),
      nombreRazonSocial: value.nombreRazonSocial,
      notario: value.notario,
      numeroProtocolo: value.numeroProtocolo,
      objetoSocial: value.objetoSocial,
      observaciones: value.observaciones,
      solicitante: value.solicitanteRef ? { id: value.solicitanteRef } as IPersona : undefined,
      activo: value.activo
    };
  }

  fromTarget(value: IEmpresaExplotacionResultados): IEmpresaExplotacionResultadosResponse {
    if (!value) {
      return value as unknown as IEmpresaExplotacionResultadosResponse;
    }
    return {
      id: value.id,
      fechaSolicitud: LuxonUtils.toBackend(value.fechaSolicitud),
      tipoEmpresa: value.tipoEmpresa,
      conocimientoTecnologia: value.conocimientoTecnologia,
      entidadRef: value.entidad?.id,
      estado: value.estado,
      fechaAprobacionCG: LuxonUtils.toBackend(value.fechaAprobacionCG),
      fechaCese: LuxonUtils.toBackend(value.fechaCese),
      fechaConstitucion: LuxonUtils.toBackend(value.fechaConstitucion),
      fechaDesvinculacion: LuxonUtils.toBackend(value.fechaDesvinculacion),
      fechaIncorporacion: LuxonUtils.toBackend(value.fechaIncorporacion),
      nombreRazonSocial: value.nombreRazonSocial,
      notario: value.notario,
      numeroProtocolo: value.numeroProtocolo,
      objetoSocial: value.objetoSocial,
      observaciones: value.observaciones,
      solicitanteRef: value.solicitante?.id,
      activo: value.activo
    };
  }
}

export const EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER = new EmpresaExplotacionResultadosResponseConverter();
