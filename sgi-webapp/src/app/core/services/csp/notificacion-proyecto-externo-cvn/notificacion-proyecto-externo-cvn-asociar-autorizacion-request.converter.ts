import { IAutorizacion } from '@core/models/csp/autorizacion';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { SgiBaseConverter } from '@sgi/framework/core';
import { INotificacionProyectoExternoCvnAsociarAutorizacionRequest } from './notificacion-proyecto-externo-cvn-asociar-autorizacion-request';
import { INotificacionProyectoExternoCVNRequest } from './notificacion-proyecto-externo-cvn-request';

class INotificacionProyectoExternoCvnAsociarAutorizacionRequestConverter
  extends SgiBaseConverter<INotificacionProyectoExternoCvnAsociarAutorizacionRequest, INotificacionProyectoExternoCVN> {
  toTarget(value: INotificacionProyectoExternoCvnAsociarAutorizacionRequest): INotificacionProyectoExternoCVN {
    if (!value) {
      return value as unknown as INotificacionProyectoExternoCVN;
    }
    return {
      id: undefined,
      titulo: undefined,
      autorizacion: { id: value.autorizacionId } as IAutorizacion,
      proyecto: undefined,
      ambitoGeografico: undefined,
      codExterno: undefined,
      datosEntidadParticipacion: undefined,
      datosResponsable: undefined,
      documento: undefined,
      entidadParticipacion: undefined,
      fechaInicio: undefined,
      fechaFin: undefined,
      gradoContribucion: undefined,
      importeTotal: undefined,
      nombrePrograma: undefined,
      porcentajeSubvencion: undefined,
      proyectoCVNId: undefined,
      responsable: undefined,
      urlDocumentoAcreditacion: undefined,
      solicitante: undefined,
    };
  }

  fromTarget(value: INotificacionProyectoExternoCVN): INotificacionProyectoExternoCvnAsociarAutorizacionRequest {
    if (!value) {
      return value as unknown as INotificacionProyectoExternoCVNRequest;
    }
    return {
      autorizacionId: value.autorizacion?.id
    };
  }
}

export const NOTIFICACION_PROYECTO_EXTERNO_CVN_ASOCIAR_AUTORIZACION_REQUEST_CONVERTER = new INotificacionProyectoExternoCvnAsociarAutorizacionRequestConverter();
