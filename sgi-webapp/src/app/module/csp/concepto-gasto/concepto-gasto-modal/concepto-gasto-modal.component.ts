import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/tipos-configuracion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const CONCEPTO_GASTO_KEY = marker('csp.concepto-gasto');
const CONCEPTO_GASTO_NOMBRE_KEY = marker('csp.concepto-gasto.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-concepto-gasto-modal',
  templateUrl: './concepto-gasto-modal.component.html',
  styleUrls: ['./concepto-gasto-modal.component.scss']
})
export class ConceptoGastoModalComponent extends
  BaseModalComponent<IConceptoGasto, ConceptoGastoModalComponent> implements OnInit {

  msgParamNombreEntity = {};
  title: string;
  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConceptoGastoModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public conceptoGasto: IConceptoGasto,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, conceptoGasto);
    if (conceptoGasto) {
      this.conceptoGasto = { ...conceptoGasto };
    } else {
      this.conceptoGasto = { activo: true } as IConceptoGasto;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      nombre: new FormControl(this.conceptoGasto?.nombre),
      descripcion: new FormControl(this.conceptoGasto?.descripcion),
    });
  }

  private setupI18N(): void {
    this.translate.get(
      CONCEPTO_GASTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.conceptoGasto.nombre) {
      this.translate.get(
        CONCEPTO_GASTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        CONCEPTO_GASTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  protected getDatosForm(): IConceptoGasto {
    const conceptoGasto = this.conceptoGasto;
    conceptoGasto.nombre = this.formGroup.get('nombre').value;
    conceptoGasto.descripcion = this.formGroup.get('descripcion').value;
    return conceptoGasto;
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.conceptoGasto?.nombre),
      descripcion: new FormControl(this.conceptoGasto?.descripcion)
    });
  }

}
