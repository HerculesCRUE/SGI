import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PERSONA_CONVERTER } from '@core/converters/sgp/persona.converter';
import { IPersonaBackend } from '@core/models/sgp/backend/persona-backend';
import { IPersona } from '@core/models/sgp/persona';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import {
  FindAllCtor,
  FindByIdCtor,
  mixinFindAll,
  mixinFindById, SgiRestBaseService
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoColectivoPublic } from 'src/app/esb/sgp/shared/select-persona-public/select-persona-public.component';


// tslint:disable-next-line: variable-name
const _PersonaMixinBase:
  FindByIdCtor<string, IPersona, IPersonaBackend> &
  FindAllCtor<IPersona, IPersonaBackend> &
  typeof SgiRestBaseService = mixinFindById(
    mixinFindAll(
      SgiRestBaseService,
      PERSONA_CONVERTER
    ),
    PERSONA_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class PersonaPublicService extends _PersonaMixinBase {
  private static readonly MAPPING = '/personas';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${PersonaPublicService.PUBLIC_PREFIX}${PersonaPublicService.MAPPING}`,
      http
    );
  }

  getFormlyModelById(id: string): Observable<any> {
    return this.http.get<any[]>(`${this.endpointUrl}/formly/${id}`);
  }

  getFormlyView(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/view`);
  }

  findAutocomplete(term: string, tipoColectivo?: TipoColectivoPublic, colectivos: string[] = []): Observable<IPersona[]> {
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
