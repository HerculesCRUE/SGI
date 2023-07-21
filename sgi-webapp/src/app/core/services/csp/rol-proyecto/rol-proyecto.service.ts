import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IRolProyectoColectivo } from '@core/models/csp/rol-proyecto-colectivo';
import { ColectivoService } from '@core/services/sgp/colectivo.service';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IRolProyectoRequest } from './rol-proyecto-request';
import { ROL_PROYECTO_REQUEST_CONVERTER } from './rol-proyecto-request.converter';
import { IRolProyectoResponse } from './rol-proyecto-response';
import { ROL_PROYECTO_RESPONSE_CONVERTER } from './rol-proyecto-response.converter';

// tslint:disable-next-line: variable-name
const _RolProyectoServiceMixinBase:
  CreateCtor<IRolProyecto, IRolProyecto, IRolProyectoRequest, IRolProyectoResponse> &
  UpdateCtor<number, IRolProyecto, IRolProyecto, IRolProyectoRequest, IRolProyectoResponse> &
  FindByIdCtor<number, IRolProyecto, IRolProyectoResponse> &
  FindAllCtor<IRolProyecto, IRolProyectoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          ROL_PROYECTO_REQUEST_CONVERTER,
          ROL_PROYECTO_RESPONSE_CONVERTER
        ),
        ROL_PROYECTO_REQUEST_CONVERTER,
        ROL_PROYECTO_RESPONSE_CONVERTER
      ),
      ROL_PROYECTO_RESPONSE_CONVERTER
    ),
    ROL_PROYECTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RolProyectoService extends _RolProyectoServiceMixinBase {
  private static readonly MAPPING = '/rolproyectos';

  constructor(protected http: HttpClient,
    private colectivoService: ColectivoService) {
    super(
      `${environment.serviceServers.csp}${RolProyectoService.MAPPING}`,
      http,
    );
  }

  findPrincipal(): Observable<IRolProyecto> {
    return this.http.get<IRolProyecto>(`${this.endpointUrl}/principal`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      }),
      map(response => {
        return response;
      })
    );

  }

  /**
   * Recupera listado de RolProyectoColectivo a partir del RolProyecto.
   * @param id RolProyecto
   * @return listado RolProyectoColectivo
   */
  findAllColectivos(id: number): Observable<SgiRestListResult<string>> {
    const endpointUrl = `${this.endpointUrl}/${id}/colectivos`;
    return this.find<string, string>(endpointUrl, null);
  }

  /**
   * Recupera listado de RolProyectoColectivo a partir del RolProyecto.
   * @param id RolProyecto
   * @return listado RolProyectoColectivo
   */
  findAllRolProyectoColectivos(id: number): Observable<SgiRestListResult<IRolProyectoColectivo>> {
    const endpointUrl = `${this.endpointUrl}/${id}/rol-proyecto-colectivos`;
    return this.find<IRolProyectoColectivo, IRolProyectoColectivo>(endpointUrl, null);
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRolProyecto>> {
    return this.find<IRolProyectoResponse, IRolProyecto>(
      `${this.endpointUrl}/todos`,
      options,
      ROL_PROYECTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar fuentes de financiación
   * @param options opciones de búsqueda.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar fuentes de financiación
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

  /**
   * Comprueba si existe una solicitudProyectoDatos asociada a una solicitud
   *
   * @param id Id de la solicitud
   */
  existsPrincipal(): Observable<boolean> {
    const url = `${this.endpointUrl}/principal`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

}
