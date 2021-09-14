import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { FormlyFieldConfig, FormlyFormOptions } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_SAVE_ERROR = marker('error.save.request.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.request.entity.success');
const MSG_UPDATE_ERROR = marker('error.update.request.entity');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

export enum ACTION_MODAL_MODE {
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}
export interface IFormlyData {
  fields: FormlyFieldConfig[];
  data: any;
  model: any;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseFormlyModalComponent implements OnInit, OnDestroy {

  ACTION_MODAL_MODE = ACTION_MODAL_MODE;

  title: string;

  protected textoCrearSuccess: string;
  protected textoCrearError: string;

  protected textoUpdateSuccess: string;
  protected textoUpdateError: string;

  protected subscriptions: Subscription[] = [];

  formGroup: FormGroup = new FormGroup({});
  formlyData: IFormlyData = {
    fields: [],
    data: {},
    model: {}
  };

  options: FormlyFormOptions = {
    formState: {
      mainModel: {},
    },
  };

  constructor(
    protected readonly translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(subscription => subscription.unsubscribe());
  }

  private setupI18N(): void {
    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);

    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...this.getGender() }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...this.getGender() }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);

    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...this.getGender() }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_ERROR,
          { entity: value, ...this.getGender() }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);
  }

  protected abstract loadFormlyData(action: ACTION_MODAL_MODE, id: string): Observable<IFormlyData>;

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  abstract saveOrUpdate(): void;

  protected abstract getKey(): string;

  protected abstract getGender(): any;
}
