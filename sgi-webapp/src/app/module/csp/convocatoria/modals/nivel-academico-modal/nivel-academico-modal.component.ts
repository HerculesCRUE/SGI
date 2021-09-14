import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';

import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';

const MSG_ERROR_LOAD = marker('error.load');

export interface NivelAcademicoModalData {
  selectedEntidades: INivelAcademico[];
}

@Component({
  templateUrl: './nivel-academico-modal.component.html',
  styleUrls: ['./nivel-academico-modal.component.scss']
})
export class NivelAcademicoModalComponent
  extends BaseModalComponent<INivelAcademico, NivelAcademicoModalComponent>
  implements OnInit {

  readonly nivelesAcademicos$ = new BehaviorSubject<INivelAcademico[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<NivelAcademicoModalComponent>,
    private nivelAcademicoService: NivelAcademicosService,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: NivelAcademicoModalData,
  ) {
    super(snackBarService, matDialogRef, null);

    this.subscriptions.push(
      this.nivelAcademicoService.findAll()
        .subscribe(response => {
          const idsNivelAcademico = this.data.selectedEntidades.map(nivelAcademico => nivelAcademico.id);
          const nivelesAcademicos = response.items.filter(nivelAcademico => !idsNivelAcademico.includes(nivelAcademico.id));
          this.nivelesAcademicos$.next(nivelesAcademicos);
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
      nivelAcademico: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getDatosForm(): INivelAcademico {
    return this.formGroup.controls.nivelAcademico.value as INivelAcademico;
  }

}
