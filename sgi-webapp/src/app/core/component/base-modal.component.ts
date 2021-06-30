import { Directive, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { Subscription } from 'rxjs';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseModalComponent<T, U> implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  protected subscriptions: Subscription[] = [];

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<U>,
    @Inject(MAT_DIALOG_DATA) public entity: T
  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
  }

  ngOnInit(): void {
    this.formGroup = this.getFormGroup();
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.matDialogRef.close(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  /**
   * Update the entity with the data from the formGroup
   */
  protected abstract getDatosForm(): T;

  /**
   * Initialize the formGroup that the modal will use
   */
  protected abstract getFormGroup(): FormGroup;

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }
}
