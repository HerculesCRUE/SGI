import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { ConfigService } from '@core/services/cnf/config.service';
import { LanguageService } from '@core/services/language.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ConfigGlobal } from '../../config-global/config-global.component';
import { ConfigSelectMultipleComponent } from '../config-select-multiple/config-select-multiple.component';

@Component({
  selector: 'sgi-config-select-multiple-cnf',
  templateUrl: './../config-select-multiple/config-select-multiple.component.html',
  styleUrls: ['./../config-select-multiple/config-select-multiple.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigSelectMultipleCnfComponent extends ConfigSelectMultipleComponent {

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    private readonly configService: ConfigService,
    private readonly languagueService: LanguageService
  ) {
    super(translate, snackBarService);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  protected getValue(key: string): Observable<IConfigValue> {
    return this.configService.findById(key);
  }

  protected updateValue(key: string, newValue: string[]): Observable<IConfigValue> {
    return this.configService.updateValue(key, JSON.stringify(newValue ?? [])).pipe(
      tap(value => {
        if (value.name == ConfigGlobal.I18N_ENABLED_LANGUAGES) {
          this.languagueService.refresh();
        }
      })
    );
  }

}
