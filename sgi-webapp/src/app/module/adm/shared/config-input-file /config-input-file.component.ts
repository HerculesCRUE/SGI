import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IResourceInfo } from '@core/models/cnf/resource-info';
import { ResourceService, triggerDownloadToUser } from '@core/services/cnf/resource.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent } from '@shared/file-upload/file-upload.component';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_SUCCESS = marker('msg.adm.resource.update.success');

@Component({
  selector: 'sgi-config-input-file',
  templateUrl: './config-input-file.component.html',
  styleUrls: ['./config-input-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigInputFileComponent implements OnInit, OnDestroy {

  resourceInfo: IResourceInfo;

  formGroup: FormGroup;

  protected subscriptions: Subscription[] = [];

  msgParamLabel = {};
  textoUpdateSuccess: string;

  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  @Input()
  set key(key: string) {
    this._key = key ?? '';
    this.loadResourceInfo(this._key);
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

  @Output()
  readonly error = new EventEmitter<Error>();

  constructor(
    private readonly translate: TranslateService,
    private readonly snackBarService: SnackBarService,
    private readonly resourceService: ResourceService
  ) { }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  save(): void {
    this.subscriptions.push(
      this.uploader.uploadSelection()
        .subscribe(
          () => {
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
            this.error.next(null);
          },
          (error) => this.error.next(error)
        )
    );
  }

  downloadResource(): void {
    this.subscriptions.push(
      this.resourceService.download(this.key).subscribe(
        (resource) => triggerDownloadToUser(resource, this.key),
        (error) => this.error.next(error)
      )
    );
  }

  hasChanges(): boolean {
    return !!this.formGroup.controls.resource?.value;
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      resource: new FormControl(null),
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

  private loadResourceInfo(key: string): void {
    this.subscriptions.push(
      this.resourceService.getResourceInfo(key).subscribe(
        (resourceInfo) => {
          this.resourceInfo = resourceInfo;
        },
        (error) => this.error.next(error)
      )
    );
  }

}
