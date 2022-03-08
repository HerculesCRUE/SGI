import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { INotificacionCVNEntidadFinanciadora } from '@core/models/csp/notificacion-cvn-entidad-financiadora';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { INotificacionCVNEntidadFinanciadoraRequest } from './notificacion-cvn-entidad-financiadora-request';
import { NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_REQUEST_CONVERTER } from './notificacion-cvn-entidad-financiadora-request.converter';
import { INotificacionCVNEntidadFinanciadoraResponse } from './notificacion-cvn-entidad-financiadora-response';
import { NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_RESPONSE_CONVERTER } from './notificacion-cvn-entidad-financiadora-response.converter';

// tslint:disable-next-line: variable-name
const _NotificacionCvnEntidadFinanciadoraMixinBase:
  CreateCtor<INotificacionCVNEntidadFinanciadora, INotificacionCVNEntidadFinanciadora,
    INotificacionCVNEntidadFinanciadoraRequest, INotificacionCVNEntidadFinanciadoraResponse> &
  UpdateCtor<number, INotificacionCVNEntidadFinanciadora, INotificacionCVNEntidadFinanciadora,
    INotificacionCVNEntidadFinanciadoraRequest, INotificacionCVNEntidadFinanciadoraResponse> &
  FindByIdCtor<number, INotificacionCVNEntidadFinanciadora, INotificacionCVNEntidadFinanciadoraResponse> &
  FindAllCtor<INotificacionCVNEntidadFinanciadora, INotificacionCVNEntidadFinanciadoraResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_REQUEST_CONVERTER,
          NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_RESPONSE_CONVERTER
        ),
        NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_REQUEST_CONVERTER,
        NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_RESPONSE_CONVERTER
      ),
      NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_RESPONSE_CONVERTER
    ),
    NOTIFICACION_CVN_ENTIDAD_FINANCIADORA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class NotificacionCvnEntidadFinanciadoraService extends _NotificacionCvnEntidadFinanciadoraMixinBase {
  private static readonly MAPPING = '/notificacionescvnentidadesfinanciadoras';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${NotificacionCvnEntidadFinanciadoraService.MAPPING}`,
      http,
    );
  }



}
