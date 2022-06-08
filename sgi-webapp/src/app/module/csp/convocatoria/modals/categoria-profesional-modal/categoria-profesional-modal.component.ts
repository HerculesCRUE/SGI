import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';

const CONVOCATORIA_CATEGORIA_PROFESIONAL_KEY = marker('csp.convocatoria.categoria-profesional');

export interface CategoriaProfesionalModalData {
  selectedEntidades: ICategoriaProfesional[];
}

@Component({
  templateUrl: './categoria-profesional-modal.component.html',
  styleUrls: ['./categoria-profesional-modal.component.scss']
})
export class CategoriaProfesionalModalComponent extends DialogFormComponent<ICategoriaProfesional> implements OnInit {

  readonly categoriasProfesionales$ = new BehaviorSubject<ICategoriaProfesional[]>([]);

  msgParamCategoriaProfesional = {};

  constructor(
    matDialogRef: MatDialogRef<CategoriaProfesionalModalComponent>,
    private categoriaProfesionalService: CategoriaProfesionalService,
    private readonly translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: CategoriaProfesionalModalData,
  ) {
    super(matDialogRef, false);

    this.subscriptions.push(
      this.categoriaProfesionalService.findAll()
        .subscribe(response => {
          const idsCategoriaProfesional = this.data.selectedEntidades.map(categoriaProfesional => categoriaProfesional.id);
          const categoriasProfesionales = response.items.filter(categoriaProfesional =>
            !idsCategoriaProfesional.includes(categoriaProfesional.id));
          this.categoriasProfesionales$.next(categoriasProfesionales);
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
      CONVOCATORIA_CATEGORIA_PROFESIONAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCategoriaProfesional = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      categoriaProfesional: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getValue(): ICategoriaProfesional {
    return this.formGroup.controls.categoriaProfesional.value as ICategoriaProfesional;
  }

}
