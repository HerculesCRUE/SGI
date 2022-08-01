import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EMPRESA_EQUIPO_EMPRENDEDOR_REQUEST_CONVERTER } from './empresa-equipo-emprendedor-request.converter';
import { IEmpresaEquipoEmprendedorResponse } from './empresa-equipo-emprendedor-response';
import { EMPRESA_EQUIPO_EMPRENDEDOR_RESPONSE_CONVERTER } from './empresa-equipo-emprendedor-response.converter';

// tslint:disable-next-line: variable-name
const _EmpresaEquipoEmprendedorMixinBase:
  FindByIdCtor<number, IEmpresaEquipoEmprendedor, IEmpresaEquipoEmprendedorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    EMPRESA_EQUIPO_EMPRENDEDOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaEquipoEmprendedorService extends _EmpresaEquipoEmprendedorMixinBase {
  private static readonly MAPPING = '/empresasequiposemprendedores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EmpresaEquipoEmprendedorService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDEmpresaEquipoEmprendedor asociados a un IEmpresaEquipoEmprendedor
   *
   * @param id Id del IEmpresaEquipoEmprendedor
   * @param entities Listado de IEmpresaEquipoEmprendedor
   */
  updateList(id: number, entities: IEmpresaEquipoEmprendedor[]): Observable<IEmpresaEquipoEmprendedor[]> {
    return this.http.patch<IEmpresaEquipoEmprendedorResponse[]>(
      `${this.endpointUrl}/${id}`,
      EMPRESA_EQUIPO_EMPRENDEDOR_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => EMPRESA_EQUIPO_EMPRENDEDOR_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
