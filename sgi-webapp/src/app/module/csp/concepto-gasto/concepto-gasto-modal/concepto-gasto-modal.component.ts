import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const CONCEPTO_GASTO_KEY = marker('csp.concepto-gasto');
const CONCEPTO_GASTO_NOMBRE_KEY = marker('csp.concepto-gasto.nombre');
const CONCEPTO_GASTO_COSTES_INDIRECTOS = marker('label.costes-indirectos');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-concepto-gasto-modal',
  templateUrl: './concepto-gasto-modal.component.html',
  styleUrls: ['./concepto-gasto-modal.component.scss']
})
export class ConceptoGastoModalComponent extends DialogActionComponent<IConceptoGasto> implements OnInit, OnDestroy {

  private readonly conceptoGasto: IConceptoGasto;
  msgParamNombreEntity = {};
  msgParamCostesIndirectos = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<ConceptoGastoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: IConceptoGasto,
    private readonly conceptoGastoService: ConceptoGastoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.conceptoGasto = { ...data };
    } else {
      this.conceptoGasto = { activo: true } as IConceptoGasto;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CONCEPTO_GASTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONCEPTO_GASTO_COSTES_INDIRECTOS,
    ).subscribe((value) => this.msgParamCostesIndirectos = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.isEdit()) {
      this.translate.get(
        CONCEPTO_GASTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
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
    }
  }

  protected getValue(): IConceptoGasto {
    const conceptoGasto = this.conceptoGasto;
    conceptoGasto.nombre = this.formGroup.get('nombre').value;
    conceptoGasto.descripcion = this.formGroup.get('descripcion').value;
    conceptoGasto.costesIndirectos = this.formGroup.get('costesIndirectos').value;
    return conceptoGasto;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.conceptoGasto?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.conceptoGasto?.descripcion ?? ''),
      costesIndirectos: new FormControl(this.conceptoGasto?.costesIndirectos, Validators.required)
    });
  }

  protected saveOrUpdate(): Observable<IConceptoGasto> {
    const conceptoGasto = this.getValue();
    return this.isEdit() ? this.conceptoGastoService.update(conceptoGasto.id, conceptoGasto) :
      this.conceptoGastoService.create(conceptoGasto);
  }
}
