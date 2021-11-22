import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISgiDynamicReport } from '@core/models/rep/sgi-dynamic-report';
import { environment } from '@env';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private static readonly MAPPING = '/report/common';

  private readonly endpointUrl: string;

  constructor(protected http: HttpClient) {
    this.endpointUrl = `${environment.serviceServers.rep}${ReportService.MAPPING}`;
  }

  downloadDynamicReport(report: ISgiDynamicReport): Observable<Blob> {
    return this.http.post(`${this.endpointUrl}/dynamic`, report
      , {
        headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
      }
    );

  }
}
