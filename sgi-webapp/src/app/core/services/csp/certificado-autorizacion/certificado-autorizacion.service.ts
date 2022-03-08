import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ICertificadoAutorizacionRequest } from './certificado-autorizacion-request';
import { CERTIFICADO_AUTORIZACION_REQUEST_CONVERTER } from './certificado-autorizacion-request.converter';
import { ICertificadoAutorizacionResponse } from './certificado-autorizacion-response';
import { CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER } from './certificado-autorizacion-response.converter';

// tslint:disable-next-line: variable-name
const _CertificadoAutorizacionMixinBase:
  CreateCtor<ICertificadoAutorizacion, ICertificadoAutorizacion, ICertificadoAutorizacionRequest, ICertificadoAutorizacionResponse> &
  UpdateCtor<number, ICertificadoAutorizacion, ICertificadoAutorizacion,
    ICertificadoAutorizacionRequest, ICertificadoAutorizacionResponse> &
  FindByIdCtor<number, ICertificadoAutorizacion, ICertificadoAutorizacionResponse> &
  FindAllCtor<ICertificadoAutorizacion, ICertificadoAutorizacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          CERTIFICADO_AUTORIZACION_REQUEST_CONVERTER,
          CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER
        ),
        CERTIFICADO_AUTORIZACION_REQUEST_CONVERTER,
        CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER
      ),
      CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER
    ),
    CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class CertificadoAutorizacionService extends _CertificadoAutorizacionMixinBase {
  private static readonly MAPPING = '/certificadosautorizaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${CertificadoAutorizacionService.MAPPING}`,
      http,
    );
  }
  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
