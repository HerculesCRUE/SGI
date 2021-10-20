import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDatosPersonales } from '@core/models/sgp/datos-personales';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IDatosPersonalesResponse } from './datos-personales/datos-personales-response';
import { DATOS_PERSONALES_RESPONSE_CONVERTER } from './datos-personales/datos-personales-response.converter';

// tslint:disable-next-line: variable-name
const _DatosPersonalesServiceMixinBase:
  FindByIdCtor<number, IDatosPersonales, IDatosPersonalesResponse> &
  FindAllCtor<IDatosPersonales, IDatosPersonalesResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      DATOS_PERSONALES_RESPONSE_CONVERTER
    ),
    DATOS_PERSONALES_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class DatosPersonalesService extends _DatosPersonalesServiceMixinBase {
  private static readonly MAPPING = '/datos-personales';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${DatosPersonalesService.MAPPING}`,
      http
    );
  }

  /**
   * Busca los datos personales de la persona
   *
   * @param personaId identificador de la persona
   * @returns datos personales de la persona
   */
  findByPersonaId(personaId: string): Observable<IDatosPersonales> {
    return this.get<IDatosPersonalesResponse>(`${this.endpointUrl}/persona/${personaId}`).pipe(
      map(response => DATOS_PERSONALES_RESPONSE_CONVERTER.toTarget(response))
    );
  }


}
