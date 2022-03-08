import { IAutorizacionWithFirstEstado } from '@core/models/csp/autorizacion-with-first-estado';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAutorizacionWithFirstEstadoResponse } from './autorizacionWithFirstEstadoResponse';

class AutorizacionWithFirstEstadoResponseConverter
  extends SgiBaseConverter<IAutorizacionWithFirstEstadoResponse, IAutorizacionWithFirstEstado> {
  toTarget(value: IAutorizacionWithFirstEstadoResponse): IAutorizacionWithFirstEstado {
    if (!value) {
      return value as unknown as IAutorizacionWithFirstEstado;
    }
    return {
      id: value.id,
      observaciones: value.observaciones,
      responsable: value.responsableRef ? { id: value.responsableRef } as IPersona : undefined,
      solicitante: { id: value.solicitanteRef } as IPersona,
      tituloProyecto: value.tituloProyecto,
      entidad: value.entidadRef ? { id: value.entidadRef } as IEmpresa : undefined,
      horasDedicacion: value.horasDedicacion,
      datosResponsable: value.datosResponsable,
      datosEntidad: value.datosEntidad,
      datosConvocatoria: value.datosConvocatoria,
      convocatoria: value.convocatoriaId ? { id: value.convocatoriaId } as IConvocatoria : undefined,
      estado: { id: value.estadoId } as IEstadoAutorizacion,
      fechaFirstEstado: LuxonUtils.fromBackend(value.fechaFirstEstado)
    };
  }

  fromTarget(value: IAutorizacionWithFirstEstado): IAutorizacionWithFirstEstadoResponse {
    if (!value) {
      return value as unknown as IAutorizacionWithFirstEstadoResponse;
    }
    return {
      id: value.id,
      observaciones: value.observaciones,
      responsableRef: value.responsable?.id,
      solicitanteRef: value.solicitante?.id,
      tituloProyecto: value.tituloProyecto,
      entidadRef: value.entidad?.id,
      horasDedicacion: value.horasDedicacion,
      datosResponsable: value.datosResponsable,
      datosEntidad: value.datosEntidad,
      datosConvocatoria: value.datosConvocatoria,
      convocatoriaId: value.convocatoria?.id,
      estadoId: value.estado.id,
      fechaFirstEstado: LuxonUtils.toBackend(value.fechaFirstEstado)
    };
  }
}

export const AUTORIZACION_WITH_FIRST_ESTADO_RESPONSE_CONVERTER = new AutorizacionWithFirstEstadoResponseConverter();
