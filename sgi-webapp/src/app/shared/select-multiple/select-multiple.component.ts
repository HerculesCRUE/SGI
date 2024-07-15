import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectCommonMultipleComponent } from '@core/component/select-common-multiple/select-common-multiple.component';

@Component({
  selector: 'sgi-select-multiple',
  templateUrl: '../../core/component/select-common-multiple/select-common-multiple.component.html',
  styleUrls: ['../../core/component/select-common-multiple/select-common-multiple.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectMultipleComponent
    }
  ]
})
export class SelectMultipleComponent extends SelectCommonMultipleComponent<any> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

}
