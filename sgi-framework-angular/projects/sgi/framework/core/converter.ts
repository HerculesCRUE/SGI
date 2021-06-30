/**
 * Converter between types
 *
 * @template S Source type
 * @template T Target type
 */
export interface SgiConverter<S, T> {
  /**
   * Convert a element of source type to target type
   *
   * @param value Source element to convert
   *
   * @returns converted element of target type
   */
  toTarget(value: S): T;
  /**
   * Convert a array of elements of source type to target type
   *
   * @param value Source element to convert
   *
   * @returns converted element of target type
   */
  toTargetArray(value: S[]): T[];
  /**
   * Convert a element of target type to source type
   *
   * @param value Target element to convert
   *
   * @returns converted element of source type
   */
  fromTarget(value: T): S;
  /**
   * Convert a array of element of target type to source type
   *
   * @param value Target element to convert
   *
   * @returns converted element of source type
   */
  fromTargetArray(value: T[]): S[];
}

/**
 * Base converted with implemented array conversion
 */
export abstract class SgiBaseConverter<S, T> implements SgiConverter<S, T> {
  abstract toTarget(value: S): T;
  abstract fromTarget(value: T): S;
  toTargetArray(value: S[]): T[] {
    return value.map((e) => this.toTarget(e));
  }
  fromTargetArray(value: T[]): S[] {
    return value.map((e) => this.fromTarget(e));
  }
}

/**
 * Converter that do a casting without any conversion.
 */
export class SgiNoConversionConverter<S, T> implements SgiConverter<S, T> {
  toTarget(value: S): T {
    return value as unknown as T;
  }
  fromTarget(value: T): S {
    return value as unknown as S;
  }
  toTargetArray(value: S[]): T[] {
    return value as unknown as T[];
  }
  fromTargetArray(value: T[]): S[] {
    return value as unknown as S[];
  }
}