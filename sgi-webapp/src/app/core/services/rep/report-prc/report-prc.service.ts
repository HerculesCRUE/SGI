import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportPrcService {
  private static readonly MAPPING = '/report/prc';

  private readonly endpointUrl: string;

  constructor(protected http: HttpClient) {
    this.endpointUrl = `${environment.serviceServers.rep}${ReportPrcService.MAPPING}`;
  }

  getInformeDetalleGrupo(anio: number, grupoId: number): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/informedetallegrupo/${anio}/${grupoId}`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    }
    );

  }

  getInformeResumenPuntuacionGrupos(anio: number): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/informeresumenpuntuaciongrupos/${anio}`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    }
    );

  }

  getDetalleProduccionInvestigador(anio, personaRef): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/informedetalleproduccioninvestigador/${anio}/${personaRef}`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    }
    );

  }
}
