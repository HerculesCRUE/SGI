import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { AgrupacionGastoConceptoService } from '@core/services/csp/agrupacio-gasto-concepto/agrupacion-gasto-concepto.service';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
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
export class AgrupacionGastoConceptoModalComponent extends
  BaseModalComponent<AgrupacionGastoConceptoModalData, AgrupacionGastoConceptoModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;
  textSaveOrUpdate: string;

  saveDisabled = false;
  agrupacionGastoConceptos$: Observable<IConceptoGasto[]>;

  msgParamAgrupacionGastoConceptoEntity = {};
  msgParamEntity = {};
  title = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<AgrupacionGastoConceptoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AgrupacionGastoConceptoModalData,
    private conceptoGastoService: ConceptoGastoService,
    private agrupacionGastoConceptoService: AgrupacionGastoConceptoService,
    private readonly translate: TranslateService,
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';

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

  protected getFormGroup(): FormGroup {
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

  protected getDatosForm(): AgrupacionGastoConceptoModalData {
    this.data.entidad = this.formGroup.get('agrupacionGastoConcepto').value;
    return this.data;
  }

}
