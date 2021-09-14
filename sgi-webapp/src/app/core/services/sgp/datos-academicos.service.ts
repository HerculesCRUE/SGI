import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDatosAcademicos } from '@core/models/sgp/datos-academicos';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IDatosAcademicosResponse } from './datos-academicos/datos-academicos-response';
import { DATOS_ACADEMICOS_RESPONSE_CONVERTER } from './datos-academicos/datos-academicos-response.converter';

// tslint:disable-next-line: variable-name
const _DatosAcademicosServiceMixinBase:
  FindByIdCtor<number, IDatosAcademicos, IDatosAcademicosResponse> &
  FindAllCtor<IDatosAcademicos, IDatosAcademicosResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      DATOS_ACADEMICOS_RESPONSE_CONVERTER
    ),
    DATOS_ACADEMICOS_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class DatosAcademicosService extends _DatosAcademicosServiceMixinBase {
  private static readonly MAPPING = '/datos-academicos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${DatosAcademicosService.MAPPING}`,
      http
    );
  }

  /**
   * Busca los datos academicos de la persona
   *
   * @param personaId identificador de la persona
   * @returns datos academicos de la persona
   */
  findByPersonaId(personaId: string): Observable<IDatosAcademicos> {
    return this.get<IDatosAcademicosResponse>(`${this.endpointUrl}/persona/${personaId}`).pipe(
      map(response => DATOS_ACADEMICOS_RESPONSE_CONVERTER.toTarget(response))
    );
  }


}
