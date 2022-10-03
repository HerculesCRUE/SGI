import { Injectable, OnDestroy } from '@angular/core';
import { environment } from '@env';
import { SgiAuthService } from '@sgi/framework/auth';
import { Settings } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, merge, Observable, Subject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { ConfigService as CspConfigService } from './csp/config.service';
import { ConfigService as EerConfigService } from './eer/config.service';
import { ConfigService as EtiConfigService } from './eti/config.service';
import { ConfigService as PiiConfigService } from './pii/config.service';
import { ConfigService as PrcConfigService } from './prc/config.service';
import { ConfigService as RelConfigService } from './rel/config.service';
import { ConfigService as RepConfigService } from './rep/config.service';
import { ConfigService as UsrConfigService } from './usr/config.service';

export interface TimeZoneConfigService {
  getTimeZone(): Observable<string>;
}

@Injectable({ providedIn: 'root' })
export class TimeZoneService implements OnDestroy {

  // tslint:disable-next-line: variable-name
  private readonly _zone$ = new BehaviorSubject<string>(environment.defaultTimeZone);

  get zone(): string {
    return this._zone$.getValue();
  }

  get zone$(): Subject<string> {
    return this._zone$;
  }

  private firstRequest = false;

  constructor(
    private logger: NGXLogger,
    authService: SgiAuthService,
    cspConfigService: CspConfigService,
    etiConfigService: EtiConfigService,
    piiConfigService: PiiConfigService,
    usrConfigService: UsrConfigService,
    repConfigService: RepConfigService,
    relConfigService: RelConfigService,
    prcConfigService: PrcConfigService,
    eerConfigService: EerConfigService
  ) {
    Settings.defaultZoneName = this._zone$.getValue();
    merge(
      this.buildRequest('CSP', cspConfigService),
      this.buildRequest('ETI', etiConfigService),
      this.buildRequest('PII', piiConfigService),
      this.buildRequest('USR', usrConfigService),
      this.buildRequest('REP', repConfigService),
      this.buildRequest('REL', relConfigService),
      this.buildRequest('PRC', prcConfigService),
      this.buildRequest('EER', eerConfigService)
    ).subscribe();
  }

  private buildRequest(name: string, service: TimeZoneConfigService): Observable<string> {
    return service.getTimeZone().pipe(
      catchError(() => EMPTY),
      tap((value) => this.evaluateTimeZone(name, value))
    );
  }

  private evaluateTimeZone(name: string, timeZone: string): void {
    if (!this.firstRequest) {
      Settings.defaultZoneName = timeZone;
      this._zone$.next(timeZone);
      this.firstRequest = true;
    }
    else {
      const prev = this._zone$.getValue();
      if (prev !== timeZone) {
        this.logger.warn(`(${name}) TimeZone mismatch: expected "${prev}", received "${timeZone}"`);
      }
    }
  }

  ngOnDestroy() {
    this._zone$.complete();
  }
}
