import { ChangeDetectionStrategy, Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SelectCommonComponent } from '@core/component/select-common/select-common.component';
import { TranslateService } from '@ngx-translate/core';

const MSG_TRUE = marker('label.si');
const MSG_FALSE = marker('label.no');

@Component({
  selector: 'sgi-select-boolean',
  templateUrl: '../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../core/component/select-common/select-common.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectBooleanComponent
    }
  ]
})
export class SelectBooleanComponent extends SelectCommonComponent<boolean> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private translateService: TranslateService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);

    // Override default sort function
    this.sortWith = ((a, b) => a.displayText.localeCompare(b.displayText));
    // Override displayWith
    this.displayWith = (option) => {
      if (option) {
        return this.translateService.instant(MSG_TRUE);
      }
      return this.translateService.instant(MSG_FALSE);
    };

    // Set options
    this.options = [true, false];
  }

}
