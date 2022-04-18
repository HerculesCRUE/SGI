import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRelacionEjecucionEconomica } from '@core/models/csp/relacion-ejecucion-economica';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable, zip } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaResponse } from './relacion-ejecucion-economica-response';
import { RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER } from './relacion-ejecucion-economica-response-converter';

@Injectable({
  providedIn: 'root'
})
export class RelacionEjecucionEconomicaService extends SgiRestBaseService {
  private static readonly MAPPING = '/relaciones-ejecucion-economica';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${RelacionEjecucionEconomicaService.MAPPING}`,
      http
    );
  }

  findRelacionesGrupos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    return this.find<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica>(
      `${this.endpointUrl}/grupos`,
      options,
      RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  findRelacionesProyectos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    return this.find<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica>(
      `${this.endpointUrl}/proyectos`,
      options,
      RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  findRelacionesProyectoSgeRef(proyectoSgeRef: string): Observable<IRelacionEjecucionEconomica[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, proyectoSgeRef)
    };

    return zip(
      this.findRelacionesGrupos(options).pipe(
        map(result => result.items)
      ),
      this.findRelacionesProyectos(options).pipe(
        map(result => result.items)
      )
    ).pipe(
      map(result => result.reduce((acc, val) => acc.concat(val), []))
    )
  }

}
