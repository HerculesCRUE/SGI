import { IAutorizacion } from '@core/models/csp/autorizacion';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAutorizacionResponse } from './autorizacion-response';

class AutorizacionResponseConverter
  extends SgiBaseConverter<IAutorizacionResponse, IAutorizacion> {
  toTarget(value: IAutorizacionResponse): IAutorizacion {
    if (!value) {
      return value as unknown as IAutorizacion;
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
    };
  }

  fromTarget(value: IAutorizacion): IAutorizacionResponse {
    if (!value) {
      return value as unknown as IAutorizacionResponse;
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
      estadoId: value.estado.id
    };
  }
}

export const AUTORIZACION_RESPONSE_CONVERTER = new AutorizacionResponseConverter();
