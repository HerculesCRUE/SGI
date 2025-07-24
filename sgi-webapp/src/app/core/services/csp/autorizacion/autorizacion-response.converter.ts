import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
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
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      responsable: value.responsableRef ? { id: value.responsableRef } as IPersona : undefined,
      solicitante: { id: value.solicitanteRef } as IPersona,
      tituloProyecto: value.tituloProyecto ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.tituloProyecto) : [],
      entidad: value.entidadRef ? { id: value.entidadRef } as IEmpresa : undefined,
      horasDedicacion: value.horasDedicacion,
      datosResponsable: value.datosResponsable,
      datosEntidad: value.datosEntidad,
      datosConvocatoria: value.datosConvocatoria ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.datosConvocatoria) : [],
      convocatoria: value.convocatoriaId ? { id: value.convocatoriaId } as IConvocatoria : null,
      estado: { id: value.estadoId } as IEstadoAutorizacion,
    };
  }

  fromTarget(value: IAutorizacion): IAutorizacionResponse {
    if (!value) {
      return value as unknown as IAutorizacionResponse;
    }
    return {
      id: value.id,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      responsableRef: value.responsable?.id,
      solicitanteRef: value.solicitante?.id,
      tituloProyecto: value.tituloProyecto ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.tituloProyecto) : [],
      entidadRef: value.entidad?.id,
      horasDedicacion: value.horasDedicacion,
      datosResponsable: value.datosResponsable,
      datosEntidad: value.datosEntidad,
      datosConvocatoria: value.datosConvocatoria ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.datosConvocatoria) : [],
      convocatoriaId: value.convocatoria?.id ?? null,
      estadoId: value.estado.id
    };
  }
}

export const AUTORIZACION_RESPONSE_CONVERTER = new AutorizacionResponseConverter();
