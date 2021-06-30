import { Directive, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Observable } from 'rxjs';
import { SelectCommonComponent } from '../select-common/select-common.component';

/** Common attributes of an SGI entity */
export interface EntityKey {
  /** ID of the entity */
  id: number | string;
  /** DisplayText of the entity */
  nombre?: string;
}

/** Base select component for selects of SGI entities. Allow loading options from a service */
@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class SelectServiceComponent<T extends EntityKey> extends SelectCommonComponent<T> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);

    // Override default compareWith
    this.compareWith = (o1, o2) => {
      if (o1 && o2) {
        return o1?.id === o2?.id;
      }
      return o1 === o2;
    };

    // Override default displayWith
    this.displayWith = (option) => option?.nombre ?? '';
  }

  /**
   * Load the options that should be rendered when loadData() is called.
   * When no externalData is provided, load the options from the service.
   */
  protected loadOptions(): Observable<T[]> {
    if (this.externalData) {
      return super.loadOptions();
    }
    return this.loadServiceOptions();
  }

  /** Load options from a service. Called by loadOptions() when externalData is false. */
  protected abstract loadServiceOptions(): Observable<T[]>;
}
