import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresaComposicionSociedad } from '@core/models/eer/empresa-composicion-sociedad';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EMPRESA_COMPOSICION_SOCIEDAD_REQUEST_CONVERTER } from './empresa-composicion-sociedad-request.converter';
import { IEmpresaComposicionSociedadResponse } from './empresa-composicion-sociedad-response';
import { EMPRESA_COMPOSICION_SOCIEDAD_RESPONSE_CONVERTER } from './empresa-composicion-sociedad-response.converter';

// tslint:disable-next-line: variable-name
const _EmpresaComposicionSociedadMixinBase:
  FindByIdCtor<number, IEmpresaComposicionSociedad, IEmpresaComposicionSociedadResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    EMPRESA_COMPOSICION_SOCIEDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaComposicionSociedadService extends _EmpresaComposicionSociedadMixinBase {
  private static readonly MAPPING = '/empresascomposicionessociedades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EmpresaComposicionSociedadService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDEmpresaComposicionSociedad asociados a un IEmpresaComposicionSociedad
   *
   * @param id Id del IEmpresaComposicionSociedad
   * @param entities Listado de IEmpresaComposicionSociedad
   */
  updateList(id: number, entities: IEmpresaComposicionSociedad[]): Observable<IEmpresaComposicionSociedad[]> {
    return this.http.patch<IEmpresaComposicionSociedadResponse[]>(
      `${this.endpointUrl}/${id}`,
      EMPRESA_COMPOSICION_SOCIEDAD_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => EMPRESA_COMPOSICION_SOCIEDAD_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
