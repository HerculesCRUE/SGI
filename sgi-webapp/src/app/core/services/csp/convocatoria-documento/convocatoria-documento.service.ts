import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { IConvocatoriaDocumentoResponse } from '@core/services/csp/convocatoria-documento/convocatoria-documento-response';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { CONVOCATORIA_DOCUMENTO_RESPONSE_CONVERTER } from './convocatoria-documento-response.converter';

const _ConvocatoriaDocumentoServiceMixinBase:
  CreateCtor<IConvocatoriaDocumento, IConvocatoriaDocumento, IConvocatoriaDocumentoResponse, IConvocatoriaDocumentoResponse> &
  UpdateCtor<number, IConvocatoriaDocumento, IConvocatoriaDocumento, IConvocatoriaDocumentoResponse, IConvocatoriaDocumentoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      CONVOCATORIA_DOCUMENTO_RESPONSE_CONVERTER,
      CONVOCATORIA_DOCUMENTO_RESPONSE_CONVERTER
    ),
    CONVOCATORIA_DOCUMENTO_RESPONSE_CONVERTER,
    CONVOCATORIA_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaDocumentoService extends _ConvocatoriaDocumentoServiceMixinBase {
  private static readonly MAPPING = '/convocatoriadocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaDocumentoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
