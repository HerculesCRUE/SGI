import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { ConfigSelectMultipleComponent } from '../config-select-multiple/config-select-multiple.component';

@Component({
  selector: 'sgi-config-select-multiple-csp',
  templateUrl: './../config-select-multiple/config-select-multiple.component.html',
  styleUrls: ['./../config-select-multiple/config-select-multiple.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigSelectMultipleCspComponent extends ConfigSelectMultipleComponent {

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    private readonly configService: ConfigService
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

  protected updateValue(key: string, newValue: string): Observable<IConfigValue> {
    return this.configService.updateValue(key, newValue);
  }

}
