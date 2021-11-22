import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ESTADO_GASTO_PROYECTO_CONVERTER } from '@core/converters/csp/estado-gasto-proyecto.converter';
import { IEstadoGastoProyectoBackend } from '@core/models/csp/backend/estado-gasto-proyecto-backend';
import { IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, RSQLSgiRestFilter, SgiRestBaseService,
  SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGastoProyectoRequest } from './gasto-proyecto-request';
import { GASTO_PROYECTO_REQUEST_CONVERTER } from './gasto-proyecto-request.converter';
import { IGastoProyectoResponse } from './gasto-proyecto-response';
import { GASTO_PROYECTO_RESPONSE_CONVERTER } from './gasto-proyecto-response.converter';

// tslint:disable-next-line: variable-name
const _GastoProyectoServiceMixinBase:
  FindAllCtor<IGastoProyecto, IGastoProyectoResponse> &
  CreateCtor<IGastoProyecto, IGastoProyecto, IGastoProyectoRequest, IGastoProyectoRequest> &
  UpdateCtor<number, IGastoProyecto, IGastoProyecto, IGastoProyectoRequest, IGastoProyectoRequest> &
  typeof SgiRestBaseService =
  mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GASTO_PROYECTO_REQUEST_CONVERTER,
        GASTO_PROYECTO_RESPONSE_CONVERTER
      ),
      GASTO_PROYECTO_REQUEST_CONVERTER,
      GASTO_PROYECTO_RESPONSE_CONVERTER
    ),
    GASTO_PROYECTO_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class GastoProyectoService extends _GastoProyectoServiceMixinBase {
  private static readonly MAPPING = '/gastosproyectos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GastoProyectoService.MAPPING}`,
      http,
    );
  }

  /**
   * Busca un gasto por el identificador del gasto
   *
   * @param gastoRef Identificador del gasto
   * @returns el gasto proyecto
   */
  findByGastoRef(gastoRef: string): Observable<IGastoProyecto> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('gastoRef', SgiRestFilterOperator.EQUALS, gastoRef)
    };

    return this.findAll(options).pipe(
      map(result => result.items[0])
    );
  }

  /**
 * Recupera listado de historico estado
 * @param id  gasto proyecto
 * @param options opciones de búsqueda.
 */
  findEstadoGastoProyecto(gastoProyectoId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEstadoGastoProyecto>> {
    return this.find<IEstadoGastoProyectoBackend, IEstadoGastoProyecto>(
      `${this.endpointUrl}/${gastoProyectoId}/estadosgastoproyecto`,
      options,
      ESTADO_GASTO_PROYECTO_CONVERTER
    );
  }

}
