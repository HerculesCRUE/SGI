import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PERSONA_CONVERTER } from '@core/converters/sgp/persona.converter';
import { IPersonaBackend } from '@core/models/sgp/backend/persona-backend';
import { IPersona } from '@core/models/sgp/persona';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import {
  RSQLSgiRestFilter, SgiMutableRestService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, reduce, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

@Injectable({
  providedIn: 'root'
})
export class PersonaService extends SgiMutableRestService<string, IPersonaBackend, IPersona> {
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
   * Busca todas las personas que tengan alguno de los ids de la lista,
   * dividiendo la lista de ids en lotes con el tamaño maximo de batchSize 
   * y haciendo tantas peticiones como lotes se generen para hacer la busqueda
   *
   * @param ids lista de identificadores de persona
   * @param batchSize tamaño maximo de los lotes
   * @param maxConcurrentRequests maxConcurrentBatches número máximo de llamadas paralelas para recuperar los lotes (por defecto 10)
   * @returns la lista de personas
   */
  findAllInBactchesByIdIn(ids: string[], batchSize: number, maxConcurrentRequests: number = 10): Observable<IPersona[]> {
    const batches: string[][] = [];
    for (let i = 0; i < ids.length; i += batchSize) {
      batches.push(ids.slice(i, i + batchSize));
    }

    return from(batches).pipe(
      mergeMap(batch =>
        this.findAllByIdIn(batch).pipe(
          map(response => response.items)
        ),
        maxConcurrentRequests
      ),
      reduce((acc, items) => acc.concat(items), [] as IPersona[])
    );
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

  createPersona(model: any): Observable<string | void> {
    return this.http.post<string>(`${this.endpointUrl}/formly`, model);
  }

  updatePersona(id: string, model: any): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/formly/${id}`, model);
  }

  getFormlyCreate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/create`);
  }

  getFormlyUpdate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/update`);
  }

  getFormlyModelById(id: string): Observable<any> {
    return this.http.get<any[]>(`${this.endpointUrl}/formly/${id}`);
  }

  getFormlyView(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/view`);
  }

  findAutocomplete(term: string, tipoColectivo?: TipoColectivo, colectivos: string[] = []): Observable<IPersona[]> {
    let params: HttpParams = new HttpParams();
    params = params.append('busqueda', term);
    if (colectivos.length) {
      params = params.append('colectivoId', `(${colectivos.join(',')})`);
    }
    else if (tipoColectivo) {
      params = params.append('tipoColectivo', tipoColectivo);
    }
    return this.http.get<IPersonaBackend[]>(`${this.endpointUrl}Fast`, { params }).pipe(
      map(response => PERSONA_CONVERTER.toTargetArray(response))
    );
  }
}
