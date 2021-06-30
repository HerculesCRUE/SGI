import { SgiConverter } from '@sgi/framework/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SgiRestBaseService } from '../rest-base.service';
import { Constructor, FAKE_CONVERTER } from './common';

/**
 * @template K type of ID
 * @template O type of output
 */
export interface CanFindById<K, O> {
  findById(id: K): Observable<O>;
}

/**
 * @template K type of ID
 * @template O type of output
 * @template RO type of response
 */
export type FindByIdCtor<K, O, RO> = Constructor<CanFindById<K, O>>;

/**
 * Apply findById method implementation to a class
 *
 * @template T type of incoming class to mix
 * @template K type of ID
 * @template TO type of output
 * @template RO type of response
 *
 * @param base incoming class to mix
 * @param converter converter to use between response and output
 * @returns Resulting class
 */
export function mixinFindById<T extends Constructor<SgiRestBaseService>, K, TO, RO>(
  base: T,
  converter?: SgiConverter<RO, TO>
): FindByIdCtor<K, TO, RO> & T {
  return class extends base {
    public findById(id: K): Observable<TO> {
      const conv = converter ?? FAKE_CONVERTER;
      return this.get<RO>(`${this.endpointUrl}/${id}`).pipe(
        map(response => {
          return conv?.toTarget(response) ?? response;
        })
      );
    }

    constructor(...args: any[]) { super(...args); }
  };
}
