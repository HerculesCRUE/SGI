import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IResourceInfoResponse } from './resource-info-response';
import { RESOURCE_INFO_RESPONSE_CONVERTER } from './resource-info-response.converter';

export function triggerDownloadToUser(file: Blob, fileName: string) {
  const downloadLink = document.createElement('a');
  const href = window.URL.createObjectURL(file);
  downloadLink.href = href;
  downloadLink.download = fileName;
  document.body.appendChild(downloadLink);
  downloadLink.click();
  document.body.removeChild(downloadLink);
  window.URL.revokeObjectURL(href);
}

@Injectable({
  providedIn: 'root'
})
export class ResourceService extends SgiRestBaseService {
  private static readonly MAPPING = '/resources';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.cnf}${ResourceService.MAPPING}`,
      http
    );
  }

  download(key: string): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/${key}`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    });
  }

  updateWithStatus(key: string, file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.patch(`${this.endpointUrl}/${key}`, formData, { observe: 'events', reportProgress: true });
  }

  restoreDefaultValue(key: string): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${key}/restore-default`, undefined);
  }

  getResourceInfo(key: string): Observable<IResourceInfoResponse> {
    return this.http.get<IResourceInfoResponse>(`${this.endpointUrl}/${key}/info`).pipe(
      map(response => RESOURCE_INFO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
