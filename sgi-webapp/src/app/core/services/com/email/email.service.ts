import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IEmailResponse } from './email-response';

// tslint:disable-next-line: variable-name
const _EmailServiceMixinBase:
  FindByIdCtor<number, IEmailResponse, IEmailResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    null,
  );

@Injectable({
  providedIn: 'root'
})
export class EmailService extends _EmailServiceMixinBase {
  private static readonly MAPPING = '/emails';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.com}${EmailService.MAPPING}`,
      http
    );
  }

  findGenericEmailtTextById(id: number): Observable<IGenericEmailText> {
    return this.findById(id).pipe(
      map((email) => {
        return {
          id: email.id,
          content: email.params.find(param => param.name === 'GENERIC_CONTENT_TEXT')?.value,
          subject: email.params.find(param => param.name === 'GENERIC_SUBJECT')?.value,
          recipients: email.recipients
        };
      })
    );
  }
}
