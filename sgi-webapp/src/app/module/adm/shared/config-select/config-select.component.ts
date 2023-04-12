import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { KeyValue } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConfigValue } from '@core/models/cnf/config-value';
import { ConfigService } from '@core/services/cnf/config.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_SUCCESS = marker('msg.adm.config.update.success');

@Component({
  selector: 'sgi-config-select',
  templateUrl: './config-select.component.html',
  styleUrls: ['./config-select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigSelectComponent implements OnInit, OnDestroy {

  configValue: IConfigValue;

  formGroup: FormGroup;

  protected subscriptions: Subscription[] = [];

  msgParamLabel = {};
  textoUpdateSuccess: string;

  @Input()
  set key(key: string) {
    this._key = key ?? '';
    this.loadConfigValue(this._key);
  }
  get key(): string {
    return this._key;
  }
  // tslint:disable-next-line: variable-name
  private _key = '';

  @Input()
  set label(label: string) {
    this._label = label ?? '';
  }
  get label(): string {
    return this._label;
  }
  // tslint:disable-next-line: variable-name
  private _label = '';

  @Input()
  set labelParams(label: any) {
    this._labelParams = label ?? {};
  }
  get labelParams(): any {
    return this._labelParams;
  }
  // tslint:disable-next-line: variable-name
  private _labelParams = {};

  @Input()
  set options$(options: Observable<KeyValue<string, string>[]>) {
    this._options$ = options ?? of([]);
  }
  get options$(): Observable<KeyValue<string, string>[]> {
    return this._options$;
  }
  // tslint:disable-next-line: variable-name
  private _options$ = of([] as KeyValue<string, string>[]);

  @Input()
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
  }
  get required(): boolean {
    return this._required;
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  @Output()
  readonly error = new EventEmitter<Error>();

  displayer = (keyValue: KeyValue<string, string>): string => keyValue.value ? this.translate.instant(keyValue.value) : '';
  comparer = (keyValue1: KeyValue<string, string>, keyValue2: KeyValue<string, string>): boolean => keyValue1?.key === keyValue2?.key;
  sorter = (keyValue1: SelectValue<KeyValue<string, string>>, keyValue2: SelectValue<KeyValue<string, string>>): number => keyValue1?.displayText.localeCompare(keyValue2?.displayText);

  constructor(
    private readonly translate: TranslateService,
    private readonly snackBarService: SnackBarService,
    private readonly configService: ConfigService
  ) { }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  save(): void {
    const newValue = this.formGroup.controls.configValue.value?.key;
    this.subscriptions.push(
      this.configService.updateValue(this.key, newValue)
        .subscribe(
          (configValue) => {
            this.configValue = configValue;
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
            this.error.next(null);
          },
          (error) => this.error.next(error)
        )
    );
  }

  hasChanges(): boolean {
    return this.configValue?.value !== this.formGroup.controls.configValue?.value?.key;
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      configValue: new FormControl(null, this.required ? [Validators.required] : []),
    });
  }

  private setupI18N(): void {
    if (this.label) {
      this.translate.get(
        this.label,
        this.labelParams
      ).subscribe((value) => this.msgParamLabel = { field: value });

      this.translate.get(
        this.label,
        { ...this.labelParams, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_SUCCESS,
            { field: value }
          );
        })
      ).subscribe((value) => this.textoUpdateSuccess = value);

    } else {
      this.msgParamLabel = {};
    }
  }

  private loadConfigValue(key: string): void {
    this.subscriptions.push(
      this.configService.findById(key).subscribe(
        (configValue) => {
          this.configValue = configValue;
          const selectOption: KeyValue<string, string> = {
            key: configValue.value,
            value: configValue.value
          };

          this.formGroup.controls.configValue.setValue(selectOption);
        },
        (error) => this.error.next(error)
      )
    );
  }

}
