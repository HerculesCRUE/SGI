import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDocumento } from '@core/models/sge/documento';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentoService extends SgiRestBaseService {
  private static readonly MAPPING = '/documentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${DocumentoService.MAPPING}`,
      http
    );
  }

  getDetail(id: string): Observable<IDocumento> {
    return this.http.get<IDocumento>(`${this.endpointUrl}/${id}`);
  }

  downloadFichero(id: string): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/${encodeURIComponent(id)}/archivo`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    });
  }
}
