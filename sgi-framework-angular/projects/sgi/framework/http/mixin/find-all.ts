import { SgiConverter } from '@sgi/framework/core';
import { Observable } from 'rxjs';
import { SgiRestBaseService } from '../rest-base.service';
import { SgiRestFindOptions, SgiRestListResult } from '../types';
import { Constructor, FAKE_CONVERTER } from './common';

/**
 * @template O type of output
 */
export interface CanFindAll<O> {
  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<O>>;
}

/**
 * @template O type of output
 * @template RO type of response
 */
export type FindAllCtor<O, RO> = Constructor<CanFindAll<O>>;

/**
 * Apply findAll method implementation to a class
 *
 * @template T type of incoming class to mix
 * @template TO type of output
 * @template RO type of response
 *
 * @param base incoming class to mix
 * @param converter converter to use between response and output
 * @returns Resulting class
 */
export function mixinFindAll<T extends Constructor<SgiRestBaseService>, TO, RO>(
  base: T,
  converter?: SgiConverter<RO, TO>
): FindAllCtor<TO, RO> & T {
  return class extends base {
    public findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<TO>> {
      const conv = converter ?? FAKE_CONVERTER;
      return this.find<RO, TO>(`${this.endpointUrl}`, options, conv);
    }

    constructor(...args: any[]) { super(...args); }
  };
}
