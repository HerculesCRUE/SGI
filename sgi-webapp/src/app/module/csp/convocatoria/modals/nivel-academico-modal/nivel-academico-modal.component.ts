import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';

const CONVOCATORIA_NIVEL_ACADEMICO_KEY = marker('csp.convocatoria.nivel-academico');

export interface NivelAcademicoModalData {
  selectedEntidades: INivelAcademico[];
}

@Component({
  templateUrl: './nivel-academico-modal.component.html',
  styleUrls: ['./nivel-academico-modal.component.scss']
})
export class NivelAcademicoModalComponent extends DialogFormComponent<INivelAcademico> implements OnInit {

  readonly nivelesAcademicos$ = new BehaviorSubject<INivelAcademico[]>([]);

  msgParamNivelAcademico = {};

  constructor(
    matDialogRef: MatDialogRef<NivelAcademicoModalComponent>,
    private nivelAcademicoService: NivelAcademicosService,
    private readonly translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: NivelAcademicoModalData,
  ) {
    super(matDialogRef, false);

    this.subscriptions.push(
      this.nivelAcademicoService.findAll()
        .subscribe(response => {
          const idsNivelAcademico = this.data.selectedEntidades.map(nivelAcademico => nivelAcademico.id);
          const nivelesAcademicos = response.items.filter(nivelAcademico => !idsNivelAcademico.includes(nivelAcademico.id));
          this.nivelesAcademicos$.next(nivelesAcademicos);
        },
          this.processError
        ));
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_NIVEL_ACADEMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNivelAcademico = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nivelAcademico: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getValue(): INivelAcademico {
    return this.formGroup.controls.nivelAcademico.value as INivelAcademico;
  }

}
