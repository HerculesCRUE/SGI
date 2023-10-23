import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { ConfigService } from '@core/services/cnf/config.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { ConfigInputTextComponent } from '../config-input-text/config-input-text.component';

@Component({
  selector: 'sgi-config-input-text-cnf',
  templateUrl: './../config-input-text/config-input-text.component.html',
  styleUrls: ['./../config-input-text/config-input-text.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigInputTextCnfComponent extends ConfigInputTextComponent {

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    protected readonly configService: ConfigService
  ) {
    super(translate, snackBarService);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  protected getValue(key: string): Observable<IConfigValue> {
    return this.configService.findById(key);
  }

  protected updateValue(key: string, newValue: string): Observable<IConfigValue> {
    return this.configService.updateValue(key, newValue);
  }

}
