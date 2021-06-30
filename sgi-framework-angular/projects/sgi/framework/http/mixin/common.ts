import { SgiConverter } from '@sgi/framework/core';

/**
 * Mutable type constructor for Mixings
 */
export type Constructor<T> = new (...args: any[]) => T;

/**
 * An uninitialized converter to bypass type checking
 */
export const FAKE_CONVERTER: SgiConverter<any, any> = undefined;
