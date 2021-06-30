import { SgiConverter } from '@sgi/framework/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SgiRestBaseService } from '../rest-base.service';
import { Constructor, FAKE_CONVERTER } from './common';

/**
 * @template K type of ID
 * @template I type of input
 * @template O type of output
 */
export interface CanUpdate<K, I, O> {
  update(id: K, element: I): Observable<O>;
}

/**
 * @template K type of ID
 * @template I type of input
 * @template O type of output
 * @template RI type of request
 * @template RO type of response
 */
export type UpdateCtor<K, I, O, RI, RO> = Constructor<CanUpdate<K, I, O>>;

/**
 * Apply update method implementation to a class
 *
 * @template T type of incoming class to mix
 * @template K type of ID
 * @template TI type of input
 * @template TO type of output
 * @template RI type of request
 * @template RO type of response
 *
 * @param base incoming class to mix
 * @param converterInput converter to use between input and request
 * @param converterOutput converter to use between response and output
 * @returns Resulting class
 */
export function mixinUpdate<T extends Constructor<SgiRestBaseService>, K, TI, TO, RI, RO>(
  base: T,
  converterInput?: SgiConverter<RI, TI>,
  converterOutput?: SgiConverter<RO, TO>
): UpdateCtor<K, TI, TO, RI, RO> & T {
  return class extends base {
    public update(id: K, element: TI): Observable<TO> {
      const converterIn = converterInput ?? FAKE_CONVERTER;
      const converterOut = converterOutput ?? FAKE_CONVERTER;
      return this.put<RI, RO>(`${this.endpointUrl}/${id}`, converterIn?.fromTarget(element) ?? element).pipe(
        map(response => {
          return converterOut?.toTarget(response) ?? response;
        })
      );
    }

    constructor(...args: any[]) { super(...args); }
  };
}
