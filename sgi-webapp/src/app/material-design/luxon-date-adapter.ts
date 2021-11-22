import { Inject, Injectable, InjectionToken, OnDestroy, Optional } from '@angular/core';
import { DateAdapter, MatDateFormats, MAT_DATE_LOCALE } from '@angular/material/core';
import { TIME_ZONE } from '@core/time-zone';
import { DateTime, DateTimeOptions, Info } from 'luxon';
import { Observable, Subject, Subscription } from 'rxjs';

export const LUXON_DATE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: 'D',
  },
  display: {
    dateInput: 'D',
    monthYearLabel: 'LLL yyyy',
    dateA11yLabel: 'DD',
    monthYearA11yLabel: 'LLLL yyyy',
  },
};

export interface LuxonTime {
  hour: number;
  minute: number;
  second: number;
  milisecond: number;
}

/** Configurable options for {@see LuxonDateAdapter}. */
export interface LuxonDateAdapterOptions {
  /**
   * Turns the use of utc dates on or off.
   * Changing this will change how Angular Material components like DatePicker output dates.
   * {@default false}
   */
  useUtc: boolean;
  defaultTime: LuxonTime;
}

/** InjectionToken for LuxonDateAdapter to configure options. */
export const LUXON_DATE_ADAPTER_OPTIONS = new InjectionToken<LuxonDateAdapterOptions>(
  'LUXON_DATE_ADAPTER_OPTIONS', {
  providedIn: 'root',
  factory: LUXON_DATE_ADAPTER_OPTIONS_FACTORY
});


/** @docs-private */
export function LUXON_DATE_ADAPTER_OPTIONS_FACTORY(): LuxonDateAdapterOptions {
  return {
    useUtc: false,
    defaultTime: {
      hour: 0,
      minute: 0,
      second: 0,
      milisecond: 0
    }
  };
}

/** The default date names to use if Intl API is not available. */
const DEFAULT_DATE_NAMES = range(31, i => String(i + 1));

/** Creates an array and fills it with values. */
function range<T>(length: number, valueFunction: (index: number) => T): T[] {
  const valuesArray = Array(length);
  for (let i = 0; i < length; i++) {
    valuesArray[i] = valueFunction(i);
  }
  return valuesArray;
}

/** Adapts Luxon Dates for use with Angular Material. */
@Injectable()
export class LuxonDateAdapter extends DateAdapter<DateTime> implements OnDestroy {
  // tslint:disable-next-line: variable-name
  private _useUTC: boolean;
  // tslint:disable-next-line: variable-name
  private _timeZone: string;
  private readonly subscription: Subscription;
  // tslint:disable-next-line: variable-name
  private _defaultTime: LuxonTime;


  constructor(
    @Optional() @Inject(MAT_DATE_LOCALE) dateLocale: string,
    @Inject(TIME_ZONE) timeZone: string | Observable<string> | Subject<string>,
    @Inject(LUXON_DATE_ADAPTER_OPTIONS)
    options?: LuxonDateAdapterOptions
  ) {
    super();
    this._useUTC = options ? !!options.useUtc : false;
    this._defaultTime = options.defaultTime;

    this.setLocale(dateLocale || DateTime.local().locale);
    if (timeZone instanceof Subject || timeZone instanceof Observable) {
      this.subscription = timeZone.subscribe((value) => this._timeZone = value);
    }
    else {
      this._timeZone = timeZone;
    }
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  setLocale(locale: string) {
    super.setLocale(locale);
  }

  getYear(date: DateTime): number {
    return date.year;
  }

  getMonth(date: DateTime): number {
    // Luxon works with 1-indexed months whereas our code expects 0-indexed.
    return date.month - 1;
  }

  getDate(date: DateTime): number {
    return date.day;
  }

  getDayOfWeek(date: DateTime): number {
    // Weekday is 1-indexed, so wee need match weekNames array
    return date.weekday - 1;
  }

  getMonthNames(style: 'long' | 'short' | 'narrow'): string[] {
    return Info.months(style, { locale: this.locale });
  }

  getDateNames(): string[] {
    if (Info.features().intl) {
      // At the time of writing, Luxon doesn't offer similar
      // functionality so we have to fall back to the Intl API.
      const dtf = new Intl.DateTimeFormat(this.locale, { day: 'numeric', timeZone: 'utc' });

      return range(31, i => {
        // Format a UTC date in order to avoid DST issues.
        const date = DateTime.utc(2017, 1, i + 1).toJSDate();

        // Strip the directionality characters from the formatted date.
        return dtf.format(date).replace(/[\u200e\u200f]/g, '');
      });
    }
    return DEFAULT_DATE_NAMES;
  }

  getDayOfWeekNames(style: 'long' | 'short' | 'narrow'): string[] {
    // For any locale the first day are Monday with index = 0
    return Info.weekdays(style, { locale: this.locale });
  }

  getYearName(date: DateTime): string {
    return date.toFormat('yyyy');
  }

  getFirstDayOfWeek(): number {
    // Luxon doesn't have support for getting the first day of the week.
    return 0;
  }

  getNumDaysInMonth(date: DateTime): number {
    return date.daysInMonth;
  }

  clone(date: DateTime): DateTime {
    return DateTime.fromObject(date.toObject({ includeConfig: true }));
  }

  createDate(year: number, month: number, date: number): DateTime {
    if (month < 0 || month > 11) {
      throw Error(`Invalid month index "${month}". Month index has to be between 0 and 11.`);
    }

    if (date < 1) {
      throw Error(`Invalid date "${date}". Date has to be greater than 0.`);
    }

    // Luxon uses 1-indexed months so we need to add one to the month.
    const result =
      this._useUTC
        ? DateTime.utc(
          year, month + 1, date,
          this._defaultTime.hour, this._defaultTime.minute, this._defaultTime.second, this._defaultTime.milisecond
        )
        : DateTime.local(
          year, month + 1, date,
          this._defaultTime.hour, this._defaultTime.minute, this._defaultTime.second, this._defaultTime.milisecond
        );

    if (!this.isValid(result)) {
      throw Error(`Invalid date "${date}". Reason: "${result.invalidReason}".`);
    }

    return result.setLocale(this.locale).setZone(this._timeZone);
  }

  today(): DateTime {
    return (this._useUTC ? DateTime.utc() : DateTime.local()).setLocale(this.locale).setZone(this._timeZone);
  }

  parse(value: any, parseFormat: string): DateTime | null {
    const options: DateTimeOptions = this._getOptions();

    if (typeof value === 'string' && value.length > 0) {
      const iso8601Date = DateTime.fromISO(value, options);

      if (this.isValid(iso8601Date)) {
        return iso8601Date.set({
          hour: this._defaultTime.hour,
          minute: this._defaultTime.minute,
          second: this._defaultTime.second,
          millisecond: this._defaultTime.milisecond
        });
      }

      const fromFormat = DateTime.fromFormat(value, parseFormat, options);

      if (this.isValid(fromFormat)) {
        return fromFormat.set({
          hour: this._defaultTime.hour,
          minute: this._defaultTime.minute,
          second: this._defaultTime.second,
          millisecond: this._defaultTime.milisecond
        });
      }

      return this.invalid();
    } else if (typeof value === 'number') {
      return DateTime.fromMillis(value, options).set({
        hour: this._defaultTime.hour,
        minute: this._defaultTime.minute,
        second: this._defaultTime.second,
        millisecond: this._defaultTime.milisecond
      });
    } else if (value instanceof Date) {
      return DateTime.fromJSDate(value, options).set({
        hour: this._defaultTime.hour,
        minute: this._defaultTime.minute,
        second: this._defaultTime.second,
        millisecond: this._defaultTime.milisecond
      });
    } else if (value instanceof DateTime) {
      return DateTime.fromMillis(value.toMillis(), options).set({
        hour: this._defaultTime.hour,
        minute: this._defaultTime.minute,
        second: this._defaultTime.second,
        millisecond: this._defaultTime.milisecond
      });
    }

    return null;
  }

  format(date: DateTime, displayFormat: string): string {
    if (!this.isValid(date)) {
      throw Error('LuxonDateAdapter: Cannot format invalid date.');
    }
    return date
      .setLocale(this.locale)
      .setZone(this._timeZone)
      .toFormat(displayFormat, { timeZone: this._useUTC ? 'utc' : undefined });
  }

  addCalendarYears(date: DateTime, years: number): DateTime {
    return date.plus({ years }).setLocale(this.locale).setZone(this._timeZone);
  }

  addCalendarMonths(date: DateTime, months: number): DateTime {
    return date.plus({ months }).setLocale(this.locale).setZone(this._timeZone);
  }

  addCalendarDays(date: DateTime, days: number): DateTime {
    return date.plus({ days }).setLocale(this.locale).setZone(this._timeZone);
  }

  toIso8601(date: DateTime): string {
    return date.toISO();
  }

  /**
   * Returns the given value if given a valid Luxon or null. Deserializes valid ISO 8601 strings
   * (https://www.ietf.org/rfc/rfc3339.txt) and valid Date objects into valid DateTime and empty
   * string into null. Returns an invalid date for all other values.
   */
  deserialize(value: any): DateTime | null {
    const options = this._getOptions();
    let date;
    if (value instanceof Date) {
      date = DateTime.fromJSDate(value, options);
    }
    if (typeof value === 'string') {
      if (!value) {
        return null;
      }
      date = DateTime.fromISO(value, options);
    }
    if (date && this.isValid(date)) {
      return date.set({
        hour: this._defaultTime.hour,
        minute: this._defaultTime.minute,
        second: this._defaultTime.second,
        millisecond: this._defaultTime.milisecond
      });
    }
    return super.deserialize(value);
  }

  isDateInstance(obj: any): boolean {
    return obj instanceof DateTime;
  }

  isValid(date: DateTime): boolean {
    return date.isValid;
  }

  invalid(): DateTime {
    return DateTime.invalid('Invalid Luxon DateTime object.');
  }

  /** Gets the options that should be used when constructing a new `DateTime` object. */
  private _getOptions(): DateTimeOptions {
    return {
      zone: this._useUTC ? 'utc' : this._timeZone,
      locale: this.locale
    };
  }
}
