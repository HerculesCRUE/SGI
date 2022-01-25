import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { INotificacionProyectoExternoCVNRequest } from './notificacion-proyecto-externo-cvn-request';
import { NOTIFICACION_PROYECTO_EXTERNO_CVN_REQUEST_CONVERTER } from './notificacion-proyecto-externo-cvn-request.converter';
import { INotificacionProyectoExternoCVNResponse } from './notificacion-proyecto-externo-cvn-response';
import { NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER } from './notificacion-proyecto-externo-cvn-response.converter';

// tslint:disable-next-line: variable-name
const _INotificacionProyectoExternoCVNMixinBase:
  CreateCtor<INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVN,
    INotificacionProyectoExternoCVNRequest, INotificacionProyectoExternoCVNResponse> &
  UpdateCtor<number, INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVN,
    INotificacionProyectoExternoCVNRequest, INotificacionProyectoExternoCVNResponse> &
  FindByIdCtor<number, INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVNResponse> &
  FindAllCtor<INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVNResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          NOTIFICACION_PROYECTO_EXTERNO_CVN_REQUEST_CONVERTER,
          NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
        ),
        NOTIFICACION_PROYECTO_EXTERNO_CVN_REQUEST_CONVERTER,
        NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
      ),
      NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
    ),
    NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class NotificacionProyectoExternoCvnService extends _INotificacionProyectoExternoCVNMixinBase {
  private static readonly MAPPING = '/notificacionesproyectosexternoscvn';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${NotificacionProyectoExternoCvnService.MAPPING}`,
      http,
    );
  }
}
