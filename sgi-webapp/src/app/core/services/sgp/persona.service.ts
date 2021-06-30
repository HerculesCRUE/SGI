import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PERSONA_CONVERTER } from '@core/converters/sgp/persona.converter';
import { IPersonaBackend } from '@core/models/sgp/backend/persona-backend';
import { IPersona } from '@core/models/sgp/persona';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, SgiMutableRestService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PersonaService extends SgiMutableRestService<string, IPersonaBackend, IPersona>{
  private static readonly MAPPING = '/personas';

  constructor(protected http: HttpClient) {
    super(
      PersonaService.name,
      `${environment.serviceServers.sgp}${PersonaService.MAPPING}`,
      http,
      PERSONA_CONVERTER
    );
  }

  /**
   * Busca todas las personas que tengan alguno de los ids de la lista
   *
   * @param ids lista de identificadores de persona
   * @returns la lista de personas
   */
  findAllByIdIn(ids: string[]): Observable<SgiRestListResult<IPersona>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids)
    };

    return this.findAll(options);
  }

  /**
   * Comprueba si la persona pertenece al colectivo
   *
   * @param personaId identificador de la persona
   * @param colectivosId identificadores del colectivo
   * @returns si la persona pertenece al colectivo o no
   */
  isPersonaInColectivo(personaId: string, colectivosId: string[]): Observable<boolean> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, personaId)
        .and('colectivoId', SgiRestFilterOperator.IN, colectivosId)
    };

    return this.findAll(options).pipe(
      switchMap((result) => of(result.total > 0))
    );
  }

}
