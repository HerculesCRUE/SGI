import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectCommonComponent } from '@core/component/select-common/select-common.component';

@Component({
  selector: 'sgi-select',
  templateUrl: '../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectComponent
    }
  ]
})
export class SelectComponent extends SelectCommonComponent<any> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

}
