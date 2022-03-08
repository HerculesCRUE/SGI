import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RolProyectoColectivoService extends SgiRestBaseService {
  private static readonly MAPPING = '/rolproyectocolectivos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${RolProyectoColectivoService.MAPPING}`,
      http,
    );
  }

  findColectivosActivos(): Observable<string[]> {
    return this.find<string, string>(`${this.endpointUrl}/colectivosactivos`, null).pipe(
      map(colectivos => colectivos.items)
    );
  }

}
