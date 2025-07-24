import { HttpClient, HttpEvent, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IBloque } from '@core/models/eti/bloque';
import { FormularioTipo, IFormulario } from '@core/models/eti/formulario';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IBloqueResponse } from './bloque/bloque-response';
import { BLOQUE_RESPONSE_CONVERTER } from './bloque/bloque-response.converter';
import { IFormularioResponse } from './formulario/formulario-response';
import { FORMULARIO_RESPONSE_CONVERTER } from './formulario/formulario-response.converter';

// tslint:disable-next-line: variable-name
const _FormularioServiceMixinBase:
  FindByIdCtor<number, IFormulario, IFormularioResponse> &
  FindAllCtor<IFormulario, IFormularioResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      FORMULARIO_RESPONSE_CONVERTER
    ),
    FORMULARIO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class FormularioService extends _FormularioServiceMixinBase {

  private static readonly MAPPING = '/formularios';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${FormularioService.MAPPING}`,
      http
    );
  }

  getBloques(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IBloque>> {
    return this.find<IBloqueResponse, IBloque>(`${this.endpointUrl}/${id}/bloques`, options, BLOQUE_RESPONSE_CONVERTER);
  }

  /**
   * Actualiza el estado de la memoria o de la retrospectiva al estado final
   * correspondiente al tipo de formulario completado.
   *
   * @param memoriaId identificador de la memoria
   * @param tipoFormulario tipo de formulario
   */
  completado(memoriaId: number, tipoFormulario: FormularioTipo): Observable<void> {
    const url = `${this.endpointUrl}/completado`;
    let params = new HttpParams();
    params = params.append('memoriaId', memoriaId?.toString());
    params = params.append('tipoFormulario', tipoFormulario);

    return this.http.head<void>(url, { params });
  }

  downloadReport(formularioId: number): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/${formularioId}/report`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    });
  }

  findByCodigoFormulario(codigo: string): Observable<SgiRestListResult<IFormulario>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('codigo', SgiRestFilterOperator.LIKE_ICASE, codigo)
    };
    return this.findAll(options);
  }

  updateResourceFormularioWithStatus(formularioId: number, file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.patch(`${this.endpointUrl}/${formularioId}/report`, formData, { observe: 'events', reportProgress: true });
  }

}
