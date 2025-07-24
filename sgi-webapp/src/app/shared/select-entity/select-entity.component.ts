import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectCommonComponent } from '@core/component/select-common/select-common.component';
import { EntityKey } from '@core/component/select-service/select-service.component';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'sgi-select-entity',
  templateUrl: '../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../core/component/select-common/select-common.component.scss'],
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
    protected readonly languageService: LanguageService,
    @Self() @Optional() ngControl: NgControl,
    private translateService: TranslateService
  ) {
    super(defaultErrorStateMatcher, ngControl);

    // Override default compareWith
    this.compareWith = (o1, o2) => {
      if (o1 && o2) {
        return o1?.id === o2?.id;
      }
      return o1 === o2;
    };

    // Override default displayWith
    this.displayWith = (option) => {
      if (option) {
        if (Array.isArray(option.nombre)) {
          return this.languageService.getFieldValue(option.nombre);
        } else {
          return option.nombre ?? '';
        }
      }
      return '';
    }

    // Override default sortWith
    this.sortWith = (o1, o2) => o1?.displayText?.localeCompare(o2?.displayText);

    this.subscriptions.push(this.translateService.onLangChange.subscribe(() => this.refreshDisplayValue()));
  }

}
