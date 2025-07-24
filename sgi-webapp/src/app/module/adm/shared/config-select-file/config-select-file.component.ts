import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { KeyValue } from '@angular/common';
import { AfterViewInit, Directive, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConfigValue } from '@core/models/cnf/config-value';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ResourceUploadComponent } from '../resource-upload/resource-upload.component';
import { IResourceInfo } from '@core/models/cnf/resource-info';
import { ResourceService, triggerDownloadToUser } from '@core/services/cnf/resource.service';
import { LanguageService } from '@core/services/language.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { IFormulario } from '@core/models/eti/formulario';
import { SgiRestListResult } from '@sgi/framework/http';

const MSG_SUCCESS = marker('msg.adm.resource.update.success');
const MSG_ERROR = marker('msg.adm.resource.download.error');

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class ConfigSelectFileComponent implements OnInit, OnDestroy, AfterViewInit {

  configValue: IConfigValue;

  formGroup: FormGroup;

  resourceInfo: IResourceInfo;

  protected subscriptions: Subscription[] = [];

  msgParamLabel = {};
  textoUpdateSuccess: string;
  textoDownloadError: string;

  @Input()
  set key(key: string) {
    this._key = key ?? '';
    // this.loadConfigValue(this._key);
    this.loadOptions(this._key);
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

  @Input()
  set disabled(disable: boolean) {
    this._disabled = disable ?? false;
    if (this.formGroup) {
      this.controlDisableEnable(disable);
    }
  }
  get disabled(): boolean {
    return this._disabled;
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

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

  /** Emitter fo the user selection */
  @Output()
  readonly selectionChange: EventEmitter<any> = new EventEmitter<any>();

  displayer = (keyValue: KeyValue<string, string>): string => keyValue.value ? this.translate.instant(keyValue.value) : '';
  comparer = (keyValue1: KeyValue<string, string>, keyValue2: KeyValue<string, string>): boolean => keyValue1?.key === keyValue2?.key;
  sorter = (keyValue1: SelectValue<KeyValue<string, string>>, keyValue2: SelectValue<KeyValue<string, string>>): number => keyValue1?.displayText.localeCompare(keyValue2?.displayText);

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    protected readonly formularioService: FormularioService,
    protected readonly languageService: LanguageService
  ) {
    if (translate) {
      this.subscriptions.push(translate.onLangChange.subscribe(() => {
        this.setupI18N();
      }));
    }
  }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  ngAfterViewInit(): void {
    this.subscriptions.push(this.formGroup.controls.configValue.valueChanges.subscribe(
      (event) => {
        this.formGroup.controls.resource.setValue(null);
        if (!event?.value) {
          this.formGroup.controls.resource.disable();
        } else {
          this.formGroup.controls.resource.enable();
        }
      }
    ));
  }

  save(): void {
    const newValue = this.formGroup.controls.configValue.value?.key;
    this.subscriptions.push(
      this.upload(newValue)
        .subscribe(
          () => {
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
            this.error.next(null);
          },
          (error) => this.error.next(error)
        )
    );
  }

  hasChanges(): boolean {
    this.controlDisableEnable(this._disabled);
    return !!this.formGroup.controls.resource?.value;
  }

  private controlDisableEnable(disable: boolean) {
    if (disable) {
      this.formGroup.disable();
    } else if (this.formGroup.disabled) {
      this.formGroup.enable();
    }
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      configValue: new FormControl(null, this.required ? [Validators.required] : []),
      resource: new FormControl(null),
    });
    this.formGroup.controls.resource.disable();
  }

  private setupI18N(): void {
    if (this.label) {
      this.translate.get(
        this.label,
        this.labelParams
      ).subscribe((value) => this.msgParamLabel = { field: value });

      this.translate.get(
        this.description,
        { ...this.labelParams, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_SUCCESS,
            { field: value }
          );
        })
      ).subscribe((value) => this.textoUpdateSuccess = value);

      this.translate.get(
        this.description,
        { ...this.labelParams, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_ERROR,
            { field: value }
          );
        })
      ).subscribe((value) => this.textoDownloadError = value);

    } else {
      this.msgParamLabel = {};
    }
  }

  private loadOptions(key: string): void {
    this.subscriptions.push(
      this.getValues(key).subscribe(value => this.options$ = of(value))
    );
  }

  private getNombreReport() {
    return this.formGroup.controls.configValue.value?.value + '-' + this.languageService.getLanguage().code;
  }

  downloadResource(): void {
    this.subscriptions.push(
      this.download(this.formGroup.controls.configValue.value?.key).subscribe(
        (resource) => triggerDownloadToUser(resource, this.getNombreReport()),
        (error) => {
          this.snackBarService.showError(this.textoDownloadError);
        }
      )
    );
  }

  getTooltip(): string {
    if (this.textoTooltip()) {
      return this.textoTooltip();
    } else {
      return this.configValue?.description ? this.configValue.description : this.description
    }
  }

  protected abstract getValues(key: string): Observable<KeyValue<string, string>[]>;
  protected abstract download(key: number): Observable<Blob>;
  protected abstract upload(key: number): Observable<void>;
  protected textoTooltip?(): string;
}
