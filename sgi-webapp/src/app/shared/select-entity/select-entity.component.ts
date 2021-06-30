import { ChangeDetectionStrategy, Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectCommonComponent } from '@core/component/select-common/select-common.component';
import { EntityKey } from '@core/component/select-service/select-service.component';

@Component({
  selector: 'sgi-select-entity',
  templateUrl: '../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../core/component/select-common/select-common.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectEntityComponent
    }
  ]
})
export class SelectEntityComponent extends SelectCommonComponent<EntityKey> {

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

    // Override default sortWith
    this.sortWith = (o1, o2) => o1?.displayText?.localeCompare(o2?.displayText);
  }

}
