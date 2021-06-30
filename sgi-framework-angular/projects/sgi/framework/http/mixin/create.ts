import { SgiConverter } from '@sgi/framework/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SgiRestBaseService } from '../rest-base.service';
import { Constructor, FAKE_CONVERTER } from './common';

/**
 * @template I type of input
 * @template O type of output
 */
export interface CanCreate<I, O> {
  create(entity: I): Observable<O>;
}

/**
 * @template I type of input
 * @template O type of output
 * @template RI type of request
 * @template RO type of response
 */
export type CreateCtor<I, O, RI, RO> = Constructor<CanCreate<I, O>>;

/**
 * Apply create method implementation to a class
 *
 * @template T type of incoming class to mix
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
export function mixinCreate<T extends Constructor<SgiRestBaseService>, TI, TO, RI, RO>(
  base: T,
  converterInput?: SgiConverter<RI, TI>,
  converterOutput?: SgiConverter<RO, TO>
): CreateCtor<TI, TO, RI, RO> & T {
  return class extends base {
    public create(element: TI): Observable<TO> {
      const converterIn = converterInput ?? FAKE_CONVERTER;
      const converterOut = converterOutput ?? FAKE_CONVERTER;
      return this.post<RI, RO>(this.endpointUrl, converterIn?.fromTarget(element) ?? element).pipe(
        map(response => {
          return converterOut?.toTarget(response) ?? response;
        })
      );
    }

    constructor(...args: any[]) { super(...args); }
  };
}
