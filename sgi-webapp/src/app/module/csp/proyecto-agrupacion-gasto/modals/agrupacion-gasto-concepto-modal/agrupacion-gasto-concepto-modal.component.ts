import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const AGRUPACION_GASTO_CONCEPTO_KEY = marker('csp.agrupacion-gasto-concepto');
const CONCEPTO_GASTO_KEY = marker('title.csp.concepto-gasto.add');

export interface AgrupacionGastoConceptoModalData {
  proyectoId: number;
  agrupacionId: number;
  conceptosEliminados: IConceptoGasto[];
  titleEntity: string;
  entidad: IConceptoGasto;
  selectedEntidades: IAgrupacionGastoConcepto[];
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  selector: 'sgi-agrupacion-gasto-concepto-modal',
  templateUrl: './agrupacion-gasto-concepto-modal.component.html',
  styleUrls: ['./agrupacion-gasto-concepto-modal.component.scss']
})
export class AgrupacionGastoConceptoModalComponent extends DialogFormComponent<AgrupacionGastoConceptoModalData> implements OnInit {

  textSaveOrUpdate: string;


  agrupacionGastoConceptos$: Observable<IConceptoGasto[]>;

  msgParamAgrupacionGastoConceptoEntity = {};
  msgParamEntity = {};
  title = {};

  constructor(
    matDialogRef: MatDialogRef<AgrupacionGastoConceptoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AgrupacionGastoConceptoModalData,
    private conceptoGastoService: ConceptoGastoService,
    private readonly translate: TranslateService,
  ) {
    super(matDialogRef, !!data.entidad?.id);

    this.agrupacionGastoConceptos$ = this.conceptoGastoService.findAllAgrupacionesGastoConceptoNotInAgrupacion(data.proyectoId)
      .pipe(
        map(response => {
          const idsConceptosGastoAgrupacion = this.data.selectedEntidades.map(agrupacion => agrupacion.conceptoGasto.id);
          const conceptosGasto = response.items.filter(conceptoGasto => !idsConceptosGastoAgrupacion.includes(conceptoGasto.id));
          this.data.conceptosEliminados.forEach(conceptoGasto => conceptosGasto.push(conceptoGasto));
          return conceptosGasto;
        })
      );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.entidad?.id ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      AGRUPACION_GASTO_CONCEPTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAgrupacionGastoConceptoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      this.data.titleEntity,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = { entity: value });
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        agrupacionGastoConcepto: new FormControl(
          {
            value: this.data.entidad,
            disabled: this.data.entidad || this.data.readonly
          },
          [
            Validators.required,
            IsEntityValidator.isValid
          ]),
      },
    );

    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }

  protected getValue(): AgrupacionGastoConceptoModalData {
    this.data.entidad = this.formGroup.get('agrupacionGastoConcepto').value;
    return this.data;
  }

}
