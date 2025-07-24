import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env";
import { SgiRestBaseService } from "@sgi/framework/http";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FormlyDataSGEService extends SgiRestBaseService {
  private static readonly MAPPING = '/formly/services';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${FormlyDataSGEService.MAPPING}`,
      http
    );
  }

  getData(service: string, params?: HttpParams): Observable<any> {
    return this.http.get(`${this.endpointUrl}/${service}`, { params });
  }

}