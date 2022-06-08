import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_EQUIPO_INSTRUMENTAL_ENLACE_KEY = marker('csp.grupo-enlace.enlace');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoEnlaceModalData {
  titleEntity: string;
  selectedEntidades: IGrupoEnlace[];
  entidad: IGrupoEnlace;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-enlace-modal.component.html',
  styleUrls: ['./grupo-enlace-modal.component.scss']
})
export class GrupoEnlaceModalComponent extends DialogFormComponent<GrupoEnlaceModalData> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  msgParamEnlaceEntity = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<GrupoEnlaceModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoEnlaceModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.isEdit);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_EQUIPO_INSTRUMENTAL_ENLACE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEnlaceEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.isEdit) {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        enlace: new FormControl(this.data?.entidad?.enlace, [Validators.required, Validators.maxLength(100)]),
      }
    );

    return formGroup;
  }

  protected getValue(): GrupoEnlaceModalData {
    this.data.entidad.enlace = this.formGroup.get('enlace').value;
    return this.data;
  }

}
