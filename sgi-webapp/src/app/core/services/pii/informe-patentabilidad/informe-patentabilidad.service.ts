import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInformePatentabilidad } from '@core/models/pii/informe-patentabilidad';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IInformePatentabilidadRequest } from './informe-patentabilidad-request';
import { INFORME_PATENTABILIDAD_REQUEST_CONVERTER } from './informe-patentabilidad-request.converter';
import { IInformePatentabilidadResponse } from './informe-patentabilidad-response';
import { INFORME_PATENTABILIDAD_RESPONSE_CONVERTER } from './informe-patentabilidad-response.converter';

// tslint:disable-next-line: variable-name
const _InformePatentabilidadServiceMixinBase:
  FindByIdCtor<number, IInformePatentabilidad, IInformePatentabilidadResponse> &
  CreateCtor<IInformePatentabilidad, IInformePatentabilidad, IInformePatentabilidadRequest, IInformePatentabilidadResponse> &
  UpdateCtor<number, IInformePatentabilidad, IInformePatentabilidad, IInformePatentabilidadRequest, IInformePatentabilidadResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        INFORME_PATENTABILIDAD_REQUEST_CONVERTER,
        INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
      ),
      INFORME_PATENTABILIDAD_REQUEST_CONVERTER,
      INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
    ),
    INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InformePatentabilidadService extends _InformePatentabilidadServiceMixinBase {
  private static readonly MAPPING = '/informespatentabilidad';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InformePatentabilidadService.MAPPING}`,
      http,
    );
  }

  deleteById(informePatentabilidad: IInformePatentabilidad): Observable<void> {
    const endpointUrl = `${this.endpointUrl}/${informePatentabilidad.id}`;
    return this.http.delete<void>(endpointUrl);
  }
}
