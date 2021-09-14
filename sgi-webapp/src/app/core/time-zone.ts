import { InjectionToken } from '@angular/core';
import { environment } from '@env';
import { Observable, Subject } from 'rxjs';

export const TIME_ZONE = new InjectionToken<string | Observable<string> | Subject<string>>(
  'TimeZone', {
  providedIn: 'root',
  factory: TIME_ZONE_FACTORY
});

/** @docs-private */
export function TIME_ZONE_FACTORY(): string {
  return environment.defaultTimeZone;
}
