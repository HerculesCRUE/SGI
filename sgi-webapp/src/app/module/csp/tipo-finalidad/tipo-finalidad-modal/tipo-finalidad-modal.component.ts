import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';


const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_FINALIDAD_KEY = marker('csp.tipo-finalidad');
const TIPO_FINALIDAD_NOMBRE_KEY = marker('csp.tipo-finalidad.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');
@Component({
  templateUrl: './tipo-finalidad-modal.component.html',
  styleUrls: ['./tipo-finalidad-modal.component.scss']
})
export class TipoFinalidadModalComponent extends BaseModalComponent<ITipoFinalidad, TipoFinalidadModalComponent> implements OnInit {

  textSaveOrUpdate: string;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoFinalidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tipoFinalidad: ITipoFinalidad,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoFinalidad);
    if (tipoFinalidad.id) {
      this.tipoFinalidad = { ...tipoFinalidad };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoFinalidad = { activo: true } as ITipoFinalidad;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FINALIDAD_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoFinalidad.nombre) {
      this.translate.get(
        TIPO_FINALIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_FINALIDAD_KEY,
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

  protected getDatosForm(): ITipoFinalidad {
    const tipoFinalidad = this.tipoFinalidad;
    tipoFinalidad.nombre = this.formGroup.get('nombre').value;
    tipoFinalidad.descripcion = this.formGroup.get('descripcion').value;
    return tipoFinalidad;
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoFinalidad?.nombre),
      descripcion: new FormControl(this.tipoFinalidad?.descripcion)
    });
  }

}
