import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy
} from '@angular/core';

import { I18nFieldValue } from '@core/i18n/i18n-field';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Language } from '@core/i18n/language';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

function isI18nFieldValue(field: any[]): field is I18nFieldValue[] {
  if (Array.isArray(field) && field.length) {
    return field.every(f => isI18nValue(f));
  }
  return false;
}

function isI18nValue(field: I18nFieldValue | I18nFieldValueResponse): field is I18nFieldValue {
  if (typeof field.lang === "string") {
    return false;
  }
  return true;
}

@Component({
  selector: 'sgi-i18n-display-field',
  templateUrl: './i18n-display-field.component.html',
  styleUrls: ['./i18n-display-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class I18nDisplayFieldComponent implements OnDestroy {

  private readonly _destroy = new Subject();

  @Input()
  set field(value: I18nFieldValue[] | I18nFieldValueResponse[]) {
    if (Array.isArray(value)) {
      if (isI18nFieldValue(value)) {
        this._field = value;
      }
      else {
        this._field = value.map(v => { return { lang: Language.fromCode(v.lang), 'value': v.value } })
      }
    }
    else {
      this._field = [];
    }
    this.resolveValue();
  }
  get field(): I18nFieldValue[] | I18nFieldValueResponse[] {
    return this._field;
  }
  private _field: I18nFieldValue[];

  get value(): I18nFieldValue {
    return this._value;
  }
  private _value: I18nFieldValue;

  constructor(
    private readonly changeDetectorRef: ChangeDetectorRef,
    public readonly languageService: LanguageService,
    private readonly translateService: TranslateService
  ) {
    this.languageService.languageChange$.pipe(takeUntil(this._destroy)).subscribe(() => this.resolveValue());
  }


  private resolveValue() {
    const showValue = this.languageService.getField(this._field);
    if (showValue != null) {
      this._value = showValue;
    }
    else {
      this._value = null;
    }
    this.changeDetectorRef.markForCheck();
  }

  public ngOnDestroy(): void {
    this._destroy.next();
  }
}
