import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectorRef, Component, ElementRef, Inject, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { InputI18nBaseComponent } from '@core/component/input-i18n-base/input-i18n-base.component';
import { LanguageService } from '@core/services/language.service';
import { CKEDITOR_CONFIG, CkEditorConfig } from '@shared/sgi-ckeditor-config';
import Editor from 'ckeditor5-custom-build/build/ckeditor';

let nextUniqueId = 0;

@Component({
  selector: 'sgi-i18n-ckeditor',
  templateUrl: './i18n-ckeditor.component.html',
  styleUrls: ['./i18n-ckeditor.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: I18nCkeditorComponent
    }
  ]
})
export class I18nCkeditorComponent extends InputI18nBaseComponent {

  private readonly controlType = 'sgi-i18n-ckeditor';

  /** Unique id for this input. */
  protected readonly uid = this.controlType + `-${nextUniqueId++}`;

  public readonly CkEditor = Editor;

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Self() @Optional() ngControl: NgControl,
    defaultErrorStateMatcher: ErrorStateMatcher,
    languageService: LanguageService,
    focusMonitor: FocusMonitor,
    @Inject(CKEDITOR_CONFIG) public readonly config: CkEditorConfig
  ) {
    super(changeDetectorRef, elementRef, ngControl, defaultErrorStateMatcher, languageService, focusMonitor)
  }


}
