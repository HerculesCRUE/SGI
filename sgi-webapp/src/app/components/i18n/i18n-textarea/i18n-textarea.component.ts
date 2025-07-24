import { FocusMonitor } from '@angular/cdk/a11y';
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  forwardRef,
  Input,
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
  selector: 'sgi-i18n-textarea',
  templateUrl: './i18n-textarea.component.html',
  styleUrls: ['./i18n-textarea.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: forwardRef(() => I18nTextareaComponent)
    }
  ]
})
export class I18nTextareaComponent extends InputI18nBaseComponent {

  private readonly controlType = 'sgi-i18n-textarea';

  /** Unique id for this input. */
  protected readonly uid = this.controlType + `-${nextUniqueId++}`;

  @Input()
  get cdkAutosizeMinRows() {
    return this._minRowsAutosize;
  }
  set cdkAutosizeMinRows(rows) {
    this._minRowsAutosize = rows;
  }
  private _minRowsAutosize: string;

  @Input()
  get cdkAutosizeMaxRows() {
    return this._maxRowsAutosize;
  }
  set cdkAutosizeMaxRows(rows) {
    this._maxRowsAutosize = rows;
  }
  private _maxRowsAutosize: string;

  @Input()
  get useAutosize() {
    return this._useAutosize;
  }
  set useAutosize(useAutosize) {
    this._useAutosize = useAutosize;
  }
  private _useAutosize = false;

  @Input()
  get rows() {
    return this._rows;
  }
  set rows(rows) {
    this._rows = rows;
  }
  private _rows: number;

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
