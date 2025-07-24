import { FocusMonitor } from '@angular/cdk/a11y';
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  forwardRef,
  Optional,
  Self
} from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

import { MatFormFieldControl } from '@angular/material/form-field';
import { InputI18nBaseComponent } from '@core/component/input-i18n-base/input-i18n-base.component';
import { LanguageService } from '@core/services/language.service';

let nextUniqueId = 0;

@Component({
  selector: 'sgi-i18n-input',
  templateUrl: './i18n-input.component.html',
  styleUrls: ['./i18n-input.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: forwardRef(() => I18nInputComponent)
    }
  ]
})
export class I18nInputComponent extends InputI18nBaseComponent {

  private readonly controlType = 'sgi-i18n-input';

  /** Unique id for this input. */
  protected readonly uid = this.controlType + `-${nextUniqueId++}`;

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Self() @Optional() ngControl: NgControl,
    defaultErrorStateMatcher: ErrorStateMatcher,
    languageService: LanguageService,
    focusMonitor: FocusMonitor
  ) {
    super(changeDetectorRef, elementRef, ngControl, defaultErrorStateMatcher, languageService, focusMonitor)
  }

}
