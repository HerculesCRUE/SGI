import { Directive } from '@angular/core';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { TIME_ZONE } from '@core/time-zone';
import { LuxonDateAdapter, LuxonDateAdapterOptions, LUXON_DATE_ADAPTER_OPTIONS } from './luxon-date-adapter';

function LUXON_DATE_ADAPTER_OPTIONS_FACTORY(): LuxonDateAdapterOptions {
  return {
    useUtc: false,
    defaultTime: {
      hour: 23,
      minute: 59,
      second: 59,
      milisecond: 0
    }
  };
}

@Directive({
  selector: 'mat-form-field[sgiEndDate]',
  providers: [
    {
      provide: LUXON_DATE_ADAPTER_OPTIONS,
      useFactory: LUXON_DATE_ADAPTER_OPTIONS_FACTORY
    },
    {
      provide: DateAdapter,
      useClass: LuxonDateAdapter,
      deps: [MAT_DATE_LOCALE, TIME_ZONE, LUXON_DATE_ADAPTER_OPTIONS]
    },
  ]
})
export class EndDateDirective {
  constructor() { }
}
