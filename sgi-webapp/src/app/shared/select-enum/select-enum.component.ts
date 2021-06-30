import { ChangeDetectionStrategy, Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectCommonComponent } from '@core/component/select-common/select-common.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'sgi-select-enum',
  templateUrl: '../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../core/component/select-common/select-common.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectEnumComponent
    }
  ]
})
export class SelectEnumComponent extends SelectCommonComponent<string> {

  /** Enum Map to be rendered. Key is used as value, and Value as i18n display text */
  @Input()
  get enumMap(): Map<string, string> {
    return this._enumMap;
  }
  set enumMap(newValue: Map<string, string>) {
    this._enumMap = newValue;
    const options = [];
    newValue.forEach((value, key) => options.push(key));
    this.options = options;
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _enumMap = new Map<string, string>();

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private translateService: TranslateService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);

    // Override default sort function
    this.sortWith = ((a, b) => a.displayText.localeCompare(b.displayText));

    // Override default display with
    this.displayWith = (option) => {
      return this.translateService.instant(this.enumMap.get(option));
    };
  }

}
