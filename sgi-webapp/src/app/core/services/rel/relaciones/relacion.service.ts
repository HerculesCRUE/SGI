import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRelacion } from '@core/models/rel/relacion';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor,
  mixinCreate, mixinFindAll, mixinFindById,
  mixinUpdate, RSQLSgiRestFilter, SgiRestBaseService,
  SgiRestFilterOperator, SgiRestFindOptions, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRelacionRequest } from './relacion-request';
import { RELACION_REQUEST_CONVERTER } from './relacion-request.converter';
import { IRelacionResponse } from './relacion-response';
import { RELACION_RESPONSE_CONVERTER } from './relacion-response.converter';

// tslint:disable-next-line: variable-name
const _RelacionServiceMixinBase:
  CreateCtor<IRelacion, IRelacion, IRelacionRequest, IRelacionResponse> &
  UpdateCtor<number, IRelacion, IRelacion, IRelacionRequest, IRelacionResponse> &
  FindByIdCtor<number, IRelacion, IRelacionResponse> &
  FindAllCtor<IRelacion, IRelacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          RELACION_REQUEST_CONVERTER,
          RELACION_RESPONSE_CONVERTER
        ),
        RELACION_REQUEST_CONVERTER,
        RELACION_RESPONSE_CONVERTER
      ),
      RELACION_RESPONSE_CONVERTER
    ),
    RELACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RelacionService extends _RelacionServiceMixinBase {

  private static readonly MAPPING = '/relaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.rel}${RelacionService.MAPPING}`,
      http,
    );
  }

  /**
   * Busca las relaciones con el Proyecto indicado
   * 
   * @param id del Proyecto.
   * @returns relaciones con el Proyecto indicado.
   */
  findProyectoRelaciones(id: number): Observable<IRelacion[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoRef', SgiRestFilterOperator.EQUALS, id.toString())
    };
    return this.findAll(options).pipe(map(page => page.items));
  }

  /**
   * Busca las relaciones con la Invencion indicada
   * 
   * @param id de la Invencion.
   * @returns relaciones con la Invencion indicada.
   */
  findInvencionRelaciones(id: number): Observable<IRelacion[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('invencionRef', SgiRestFilterOperator.EQUALS, id.toString())
    };
    return this.findAll(options).pipe(map(page => page.items));
  }

  /**
   * Elimina la Relacion.
   * @param id referencia del documento.
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
