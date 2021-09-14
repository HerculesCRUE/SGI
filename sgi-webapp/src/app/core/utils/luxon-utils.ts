import { DateTime } from 'luxon';

export class LuxonUtils {

  private constructor() { }

  public static toBackend(value: DateTime, asDate: boolean = false): string {
    if (typeof value === 'undefined' || value === null) {
      return value as unknown as string;
    }
    if (value instanceof DateTime) {
      if (value.isValid) {
        return asDate ? value.toISODate() : value.toUTC().toISO({ suppressMilliseconds: true });
      }
      else {
        return null;
      }
    }
    throw Error('Invalid type: ' + value);
  }

  public static fromBackend(value: string): DateTime {
    if (typeof value === 'undefined' || value === null) {
      return value as unknown as DateTime;
    }
    const date = DateTime.fromISO(value);
    if (date.isValid) {
      return date;
    }
    throw Error('Invalid date format: ' + value);
  }
}
