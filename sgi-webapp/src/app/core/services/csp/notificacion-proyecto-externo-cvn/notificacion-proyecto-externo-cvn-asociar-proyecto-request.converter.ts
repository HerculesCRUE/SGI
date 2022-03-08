import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IProyecto } from '@core/models/csp/proyecto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { INotificacionProyectoExternoCvnAsociarProyectoRequest } from './notificacion-proyecto-externo-cvn-asociar-proyecto-request';
import { INotificacionProyectoExternoCVNRequest } from './notificacion-proyecto-externo-cvn-request';

class INotificacionProyectoExternoCvnAsociarProyectoRequestConverter
  extends SgiBaseConverter<INotificacionProyectoExternoCvnAsociarProyectoRequest, INotificacionProyectoExternoCVN> {
  toTarget(value: INotificacionProyectoExternoCvnAsociarProyectoRequest): INotificacionProyectoExternoCVN {
    if (!value) {
      return value as unknown as INotificacionProyectoExternoCVN;
    }
    return {
      id: undefined,
      titulo: undefined,
      autorizacion: undefined,
      proyecto: { id: value.proyectoId } as IProyecto,
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

  fromTarget(value: INotificacionProyectoExternoCVN): INotificacionProyectoExternoCvnAsociarProyectoRequest {
    if (!value) {
      return value as unknown as INotificacionProyectoExternoCVNRequest;
    }
    return {
      proyectoId: value.proyecto?.id
    };
  }
}

export const NOTIFICACION_PROYECTO_EXTERNO_CVN_ASOCIAR_PROYECTO_REQUEST_CONVERTER = new INotificacionProyectoExternoCvnAsociarProyectoRequestConverter();
