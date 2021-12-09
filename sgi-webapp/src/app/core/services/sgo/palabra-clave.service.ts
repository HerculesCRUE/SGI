import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

// tslint:disable-next-line: variable-name
const _PalabraClaveServiceMixinBase:
  FindAllCtor<string, string> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class PalabraClaveService extends _PalabraClaveServiceMixinBase {
  private static readonly MAPPING = '/palabras-clave';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${PalabraClaveService.MAPPING}`,
      http
    );
  }

  update(palabras: string[]): Observable<void> {
    return this.http.post<void>(this.endpointUrl, palabras);
  }
}
