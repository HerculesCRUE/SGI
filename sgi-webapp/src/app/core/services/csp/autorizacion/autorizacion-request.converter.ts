import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAutorizacionRequest } from './autorizacion-request';

class AutorizacionRequestConverter
  extends SgiBaseConverter<IAutorizacionRequest, IAutorizacion> {
  toTarget(value: IAutorizacionRequest): IAutorizacion {
    if (!value) {
      return value as unknown as IAutorizacion;
    }
    return {
      id: undefined,
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.observaciones) : [],
      responsable: { id: value.responsableRef } as IPersona,
      solicitante: undefined,
      tituloProyecto: value.tituloProyecto ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.tituloProyecto) : [],
      entidad: { id: value.entidadRef } as IEmpresa,
      horasDedicacion: value.horasDedicacion,
      datosResponsable: value.datosResponsable,
      datosEntidad: value.datosEntidad,
      datosConvocatoria: value.datosConvocatoria ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.datosConvocatoria) : [],
      convocatoria: { id: value.convocatoriaId } as IConvocatoria,
      estado: undefined,
    };
  }

  fromTarget(value: IAutorizacion): IAutorizacionRequest {
    if (!value) {
      return value as unknown as IAutorizacionRequest;
    }
    return {
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.observaciones) : [],
      responsableRef: value.responsable?.id,
      tituloProyecto: value.tituloProyecto ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.tituloProyecto) : [],
      entidadRef: value.entidad?.id,
      horasDedicacion: value.horasDedicacion,
      datosResponsable: value.datosResponsable,
      datosEntidad: value.datosEntidad,
      datosConvocatoria: value.datosConvocatoria ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.datosConvocatoria) : [],
      convocatoriaId: value.convocatoria?.id
    };
  }
}

export const AUTORIZACION_REQUEST_CONVERTER = new AutorizacionRequestConverter();
