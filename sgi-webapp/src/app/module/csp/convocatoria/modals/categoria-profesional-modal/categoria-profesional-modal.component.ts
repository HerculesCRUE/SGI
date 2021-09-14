import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';

import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';

const MSG_ERROR_LOAD = marker('error.load');

export interface CategoriaProfesionalModalData {
  selectedEntidades: ICategoriaProfesional[];
}

@Component({
  templateUrl: './categoria-profesional-modal.component.html',
  styleUrls: ['./categoria-profesional-modal.component.scss']
})
export class CategoriaProfesionalModalComponent
  extends BaseModalComponent<ICategoriaProfesional, CategoriaProfesionalModalComponent>
  implements OnInit {

  readonly categoriasProfesionales$ = new BehaviorSubject<ICategoriaProfesional[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<CategoriaProfesionalModalComponent>,
    private categoriaProfesionalService: CategoriaProfesionalService,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: CategoriaProfesionalModalData,
  ) {
    super(snackBarService, matDialogRef, null);

    this.subscriptions.push(
      this.categoriaProfesionalService.findAll()
        .subscribe(response => {
          const idsCategoriaProfesional = this.data.selectedEntidades.map(categoriaProfesional => categoriaProfesional.id);
          const categoriasProfesionales = response.items.filter(categoriaProfesional =>
            !idsCategoriaProfesional.includes(categoriaProfesional.id));
          this.categoriasProfesionales$.next(categoriasProfesionales);
        },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_ERROR_LOAD);
          })
    );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();

  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      categoriaProfesional: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getDatosForm(): ICategoriaProfesional {
    return this.formGroup.controls.categoriaProfesional.value as ICategoriaProfesional;
  }

}
