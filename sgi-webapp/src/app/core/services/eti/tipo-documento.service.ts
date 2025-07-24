import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFormulario } from '@core/models/eti/formulario';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ITipoDocumentoResponse } from './tipo-documento/tipo-documento-response';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from './tipo-documento/tipo-documento-response.converter';

// tslint:disable-next-line: variable-name
const _TipoDocumentoServiceMixinBase:
  FindByIdCtor<number, ITipoDocumento, ITipoDocumentoResponse> &
  FindAllCtor<ITipoDocumento, ITipoDocumentoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      TIPO_DOCUMENTO_RESPONSE_CONVERTER
    ),
    TIPO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends _TipoDocumentoServiceMixinBase {
  private static readonly MAPPING = '/tipodocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${TipoDocumentoService.MAPPING}`,
      http
    );
  }

  findByFormulario(formulario: IFormulario): Observable<ITipoDocumento[]> {
    return this.http.get<ITipoDocumentoResponse[]>(
      `${this.endpointUrl}/formulario/${formulario.id}`
    ).pipe(
      map(response => TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
