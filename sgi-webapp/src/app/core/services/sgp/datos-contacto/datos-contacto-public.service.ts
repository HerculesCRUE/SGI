import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDatosContacto } from '@core/models/sgp/datos-contacto';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IDatosContactoResponse } from './datos-contacto-response';
import { DATOS_CONTACTO_RESPONSE_CONVERTER } from './datos-contacto-response.converter';

@Injectable({
  providedIn: 'root'
})
export class DatosContactoPublicService extends SgiRestBaseService {
  private static readonly MAPPING = '/datos-contacto';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${DatosContactoPublicService.PUBLIC_PREFIX}${DatosContactoPublicService.MAPPING}`,
      http
    );
  }

  /**
   * Busca los datos de contacto de la persona
   *
   * @param personaId identificador de la persona
   * @returns datos de contacto de la persona
   */
  findByPersonaId(personaId: string): Observable<IDatosContacto> {
    return this.get<IDatosContactoResponse>(`${this.endpointUrl}/persona/${personaId}`).pipe(
      map(response => DATOS_CONTACTO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
