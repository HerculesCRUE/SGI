import { Inject, LOCALE_ID, Pipe, PipeTransform } from '@angular/core';
import { DateTime, Info } from 'luxon';

@Pipe({ name: 'luxon', pure: true })
export class LuxonDatePipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private locale: string) { }

  /**
   * @param value The date expression: a `Date` object,  a number
   * (milliseconds since UTC epoch), or an ISO string (https://www.w3.org/TR/NOTE-datetime).
   * @param format The date/time components to include, using predefined options or a
   * custom format string.
   * @param timezone A timezone offset (such as `'+0430'`), or a standard
   * UTC/GMT or continental US timezone abbreviation.
   * When not supplied, uses the end-user's local system timezone.
   * @param locale A locale code for the locale format rules to use.
   * When not supplied, uses the value of `LOCALE_ID`, which is `en-US` by default.
   * See [Setting your app locale](guide/i18n#setting-up-the-locale-of-your-app).
   * @returns A date string in the desired format.
   */
  transform(value: DateTime | Date | string | number, format?: string, timezone?: string, locale?: string): string
    | null;
  transform(value: null | undefined, format?: string, timezone?: string, locale?: string): null;
  transform(
    value: DateTime | Date | string | number | null | undefined, format?: string, timezone?: string,
    locale?: string): string | null;
  transform(
    value: DateTime | Date | string | number | null | undefined, format = 'mediumDate', timezone?: string,
    locale?: string): string | null {
    if (value == null || value === '' || value !== value) {
      return null;
    }
    try {
      let luxonDate: DateTime;
      if (value instanceof DateTime) {
        luxonDate = value;
      }
      if (value instanceof Date) {
        luxonDate = DateTime.fromJSDate(value);
      }
      if (typeof value === 'string') {
        luxonDate = DateTime.fromISO(value);
      }
      if (typeof value === 'number') {
        luxonDate = DateTime.fromMillis(value);
      }
      if (luxonDate && luxonDate.isValid) {
        return luxonDate.toFormat(
          this.getLuxonFormat(format),
          {
            locale: locale || this.locale,
            timeZone: Info.normalizeZone(timezone).name
          }
        );
      }
      return null;
    } catch (error) {
      throw Error(`InvalidPipeArgument: '${error.message}' for pipe 'LuxonDatePipe'`);
    }
  }

  private getLuxonFormat(fmt: string) {
    switch (fmt) {
      case 'short':
        return 'F';
      case 'medium':
        return 'FF';
      case 'long':
        return 'FFF';
      case 'full':
        return 'FFFF';
      case 'shortDate':
        return 'D';
      case 'mediumDate':
        return 'DD';
      case 'longDate':
        return 'DDD';
      case 'fullDate':
        return 'DDDD';
      case 'shortTime':
        return 'T';
      case 'mediumTime':
        return 'TT';
      case 'longTime':
        return 'TTT';
      case 'fullTime':
        return 'TTTT';
    }
    return fmt;
  }
}
