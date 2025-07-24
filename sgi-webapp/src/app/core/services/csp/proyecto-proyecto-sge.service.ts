import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PROYECTO_SGE_CONVERTER } from '@core/converters/csp/proyecto-proyecto-sge.converter';
import { IProyectoProyectoSgeBackend } from '@core/models/csp/backend/proyecto-proyecto-sge-backend';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiMutableRestService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoProyectoSgeService
  extends SgiMutableRestService<number, IProyectoProyectoSgeBackend, IProyectoProyectoSge> {
  private static readonly MAPPING = '/proyecto-proyectos-sge';

  constructor(protected http: HttpClient) {
    super(
      ProyectoProyectoSgeService.name,
      `${environment.serviceServers.csp}${ProyectoProyectoSgeService.MAPPING}`,
      http,
      PROYECTO_PROYECTO_SGE_CONVERTER
    );
  }

  findByProyectoSgeId(id: string): Observable<SgiRestListResult<IProyectoProyectoSge>> {
    const queryOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, id)
    };

    return this.findAll(queryOptions);
  }

  /**
     * Comprueba si la relacion entre el proyecto y el proyecto SGE es eliminable en el SGI
     *
     * @param id Id del IProyectoProyectoSge
     */
  isEliminable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/eliminable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
