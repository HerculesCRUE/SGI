import { ChangeDetectionStrategy, Component } from '@angular/core';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { I18nConfigService } from '@core/services/cnf/i18n-config.service';
import { LanguageService } from '@core/services/language.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { ConfigI18nInputTextComponent } from '../config-i18n-input-text/config-i18n-input-text.component';

@Component({
  selector: 'sgi-config-i18n-input-text-cnf',
  templateUrl: './../config-i18n-input-text/config-i18n-input-text.component.html',
  styleUrls: ['./../config-i18n-input-text/config-i18n-input-text.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigI18nInputTextCnfComponent extends ConfigI18nInputTextComponent {

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    protected readonly configService: I18nConfigService,
    protected readonly languageService: LanguageService
  ) {
    super(translate, snackBarService, languageService);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  protected getValue(key: string): Observable<II18nConfigValue> {
    return this.configService.findById(key);
  }

  protected updateValue(key: string, newValue: II18nConfigValue): Observable<II18nConfigValue> {
    return this.configService.updateValue(key, newValue);
  }

}
