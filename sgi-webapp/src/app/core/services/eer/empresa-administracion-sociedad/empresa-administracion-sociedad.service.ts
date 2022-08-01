import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresaAdministracionSociedad } from '@core/models/eer/empresa-administracion-sociedad';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EMPRESA_ADMINISTRACION_SOCIEDAD_REQUEST_CONVERTER } from './empresa-administracion-sociedad-request.converter';
import { IEmpresaAdministracionSociedadResponse } from './empresa-administracion-sociedad-response';
import { EMPRESA_ADMINISTRACION_SOCIEDAD_RESPONSE_CONVERTER } from './empresa-administracion-sociedad-response.converter';

// tslint:disable-next-line: variable-name
const _EmpresaAdministracionSociedadMixinBase:
  FindByIdCtor<number, IEmpresaAdministracionSociedad, IEmpresaAdministracionSociedadResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    EMPRESA_ADMINISTRACION_SOCIEDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaAdministracionSociedadService extends _EmpresaAdministracionSociedadMixinBase {
  private static readonly MAPPING = '/empresasadministracionessociedades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EmpresaAdministracionSociedadService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDEmpresaAdministracionSociedad asociados a un IEmpresaAdministracionSociedad
   *
   * @param id Id del IEmpresaAdministracionSociedad
   * @param entities Listado de IEmpresaAdministracionSociedad
   */
  updateList(id: number, entities: IEmpresaAdministracionSociedad[]): Observable<IEmpresaAdministracionSociedad[]> {
    return this.http.patch<IEmpresaAdministracionSociedadResponse[]>(
      `${this.endpointUrl}/${id}`,
      EMPRESA_ADMINISTRACION_SOCIEDAD_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => EMPRESA_ADMINISTRACION_SOCIEDAD_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
