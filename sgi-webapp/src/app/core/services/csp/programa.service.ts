import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPrograma } from '@core/models/csp/programa';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { IProgramaResponse } from './programa/programa-response';
import { PROGRAMA_RESPONSE_CONVERTER } from './programa/programa-response.converter';

// tslint:disable-next-line: variable-name
const _ProgramaServiceMixinBase:
  CreateCtor<IPrograma, IPrograma, IProgramaResponse, IProgramaResponse> &
  UpdateCtor<number, IPrograma, IPrograma, IProgramaResponse, IProgramaResponse> &
  FindByIdCtor<number, IPrograma, IProgramaResponse> &
  FindAllCtor<IPrograma, IProgramaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROGRAMA_RESPONSE_CONVERTER,
          PROGRAMA_RESPONSE_CONVERTER
        ),
        PROGRAMA_RESPONSE_CONVERTER,
        PROGRAMA_RESPONSE_CONVERTER
      ),
      PROGRAMA_RESPONSE_CONVERTER
    ),
    PROGRAMA_RESPONSE_CONVERTER

  );

@Injectable({
  providedIn: 'root'
})
export class ProgramaService extends _ProgramaServiceMixinBase {

  private static readonly MAPPING = '/programas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProgramaService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IProgramaResponse, IPrograma>(
      `${this.endpointUrl}/plan/todos`,
      options,
      PROGRAMA_RESPONSE_CONVERTER
    );
  }

  findAllPlan(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IProgramaResponse, IPrograma>(
      `${this.endpointUrl}/plan`,
      options,
      PROGRAMA_RESPONSE_CONVERTER
    );
  }

  findAllHijosPrograma(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IProgramaResponse, IPrograma>(
      `${this.endpointUrl}/${id}/hijos`,
      options,
      PROGRAMA_RESPONSE_CONVERTER
    );
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  activate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }
}
