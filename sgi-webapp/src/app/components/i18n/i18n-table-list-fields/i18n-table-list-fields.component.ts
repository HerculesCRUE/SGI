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
  selector: 'sgi-i18n-table-list-fields',
  templateUrl: './i18n-table-list-fields.component.html',
  styleUrls: ['./i18n-table-list-fields.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class I18nTableListFieldsComponent implements OnDestroy {

  private readonly _destroy = new Subject();

  @Input()
  set fields(values: I18nFieldValue[][] | I18nFieldValueResponse[][]) {
    if (Array.isArray(values)) {
      this._fields = [];
      values.forEach(value => {
        if (Array.isArray(value)) {
          if (isI18nFieldValue(value)) {
            this._fields.push(value);
          } else {
            this._fields.push(value.map(v => { return { lang: Language.fromCode(v.lang), 'value': v.value } }));
          }
        }
      });
    } else {
      this._fields = [];
    }
    this.resolveValues();
  }
  get fields(): I18nFieldValue[][] | I18nFieldValueResponse[][] {
    return this._fields;
  }
  private _fields: I18nFieldValue[][];

  get values(): I18nFieldValue[] {
    return this._values;
  }
  private _values: I18nFieldValue[];

  constructor(
    private readonly changeDetectorRef: ChangeDetectorRef,
    public readonly languageService: LanguageService
  ) {
    this.languageService.languageChange$.pipe(takeUntil(this._destroy)).subscribe(() => this.resolveValues());
  }


  private resolveValues() {
    this._values = [];

    this._fields?.forEach(field => {
      this._values.push(this.languageService.getField(field))
    });

    this.changeDetectorRef.markForCheck();
  }

  public ngOnDestroy(): void {
    this._destroy.next();
  }
}
