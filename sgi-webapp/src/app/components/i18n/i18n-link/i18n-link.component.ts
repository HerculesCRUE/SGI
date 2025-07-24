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
  selector: 'sgi-i18n-link',
  templateUrl: './i18n-link.component.html',
  styleUrls: ['./i18n-link.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class I18nLinkComponent implements OnDestroy {

  private readonly _destroy = new Subject();

  @Input()
  set link(value: I18nFieldValue[] | I18nFieldValueResponse[]) {
    if (Array.isArray(value)) {
      if (isI18nFieldValue(value)) {
        this._link = value;
      }
      else {
        this._link = value.map(v => { return { lang: Language.fromCode(v.lang), 'value': v.value } })
      }
    }
    else {
      this._link = [];
    }
    this.resolveValue();
  }
  get link(): I18nFieldValue[] | I18nFieldValueResponse[] {
    return this._link;
  }
  private _link: I18nFieldValue[];

  get value(): I18nFieldValue {
    return this._value;
  }
  private _value: I18nFieldValue;

  @Input()
  get href() {
    return this._href;
  }
  set href(href) {
    this._href = href;
  }
  private _href: string;

  constructor(
    private readonly changeDetectorRef: ChangeDetectorRef,
    public readonly languageService: LanguageService
  ) {
    this.languageService.languageChange$.pipe(takeUntil(this._destroy)).subscribe(() => this.resolveValue());
  }


  private resolveValue() {
    const showValue = this.languageService.getField(this._link);
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
