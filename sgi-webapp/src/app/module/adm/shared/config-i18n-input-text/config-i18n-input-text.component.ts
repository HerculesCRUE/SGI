import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Directive, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { LanguageService } from '@core/services/language.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_SUCCESS = marker('msg.adm.config.update.success');

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class ConfigI18nInputTextComponent implements OnInit, OnDestroy {

  configValue: II18nConfigValue;

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
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
  }
  get required(): boolean {
    return this._required;
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  @Input()
  set info(info: string) {
    this._info = info ?? null;
  }
  get info(): string {
    return this._info;
  }
  // tslint:disable-next-line: variable-name
  private _info = null;

  @Input()
  set description(description: string) {
    this._description = description ?? '';
  }
  get description(): string {
    return this._description;
  }
  // tslint:disable-next-line: variable-name
  private _description = '';

  @Output()
  readonly error = new EventEmitter<Error>();

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    protected readonly languageService: LanguageService
  ) { }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  save(): void {
    this.configValue.value = this.formGroup.controls.configValue.value;
    this.subscriptions.push(
      this.updateValue(this.key, this.configValue)
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
    if (this.configValue) {
      const original: I18nFieldValue[] = Array.isArray(this.configValue.value) ? this.configValue.value : [];
      const current: I18nFieldValue[] = Array.isArray(this.formGroup.controls.configValue?.value)
        ? this.formGroup.controls.configValue.value : [];

      if (original.length !== current.length) {
        return true;
      }

      return original.some((orig, index) => {
        const curr = current[index];
        return orig.lang !== curr.lang || orig.value !== curr.value;
      });
    }
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      configValue: new FormControl([], this.required ? [I18nValidators.required] : []),
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
      this.getValue(key).subscribe(
        (configValue) => {
          this.configValue = configValue;
          this.formGroup.controls.configValue.setValue(this.configValue.value);
        },
        (error) => this.error.next(error)
      )
    );
  }

  protected abstract getValue(key: string): Observable<II18nConfigValue>;
  protected abstract updateValue(key: string, newValue: II18nConfigValue): Observable<II18nConfigValue>;

}
