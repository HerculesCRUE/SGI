import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, from, of } from 'rxjs';
import { mergeMap, toArray } from 'rxjs/operators';
import { IRowConfig } from '../../ejecucion-economica-formulario/desglose-economico.fragment';

const IMPORTE_INSCRIPCION_KEY = marker('csp.ejecucion-economica.facturas-justificantes.importe-inscripcion');
const PROYECTO_KEY = marker('csp.ejecucion-economica.facturas-justificantes.proyecto-sgi');

export interface DatoEconomicoDetalleModalData extends IDatoEconomicoDetalle {
  proyectosSgiIds: number[];
  proyecto: IProyecto;
  gastoProyecto: IGastoProyecto;
  vinculacion: string;
  rowConfig: IRowConfig;
}

@Component({
  templateUrl: './viajes-dietas-modal.component.html',
  styleUrls: ['./viajes-dietas-modal.component.scss']
})
export class ViajesDietasModalComponent extends DialogFormComponent<DatoEconomicoDetalleModalData> implements OnInit {

  msgParamImporteInscripcion = {};
  msgParamProyecto = {};

  proyectos$: Observable<IProyecto[]>;

  constructor(
    matDialogRef: MatDialogRef<ViajesDietasModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DatoEconomicoDetalleModalData,
    private proyectoService: ProyectoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.gastoProyecto?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initProyectos();
  }

  protected buildFormGroup(): FormGroup {
    const proyecto = this.data.gastoProyecto?.proyectoId ? { id: this.data.gastoProyecto?.proyectoId } as IProyecto : undefined;

    return new FormGroup(
      {
        proyecto: new FormControl({ value: proyecto, disabled: !!this.data.gastoProyecto?.id }),
        fechaCongreso: new FormControl(this.data.gastoProyecto?.fechaCongreso),
        importeInscripcion: new FormControl(this.data.gastoProyecto?.importeInscripcion,
          [
            Validators.min(0),
            Validators.max(2_147_483_647)
          ]),
        observaciones: new FormControl(this.data.gastoProyecto?.observaciones,
          [
            Validators.maxLength(2000)
          ])
      }
    );
  }

  protected getValue(): DatoEconomicoDetalleModalData {
    if (!this.formGroup.controls.proyecto.disabled) {
      this.data.proyecto = this.formGroup.controls.proyecto.value;
    }
    if (!this.data.gastoProyecto) {
      this.data.gastoProyecto = { gastoRef: this.data.id } as IGastoProyecto;
    }
    this.data.gastoProyecto.proyectoId = this.data.proyecto?.id ?? this.data.gastoProyecto?.proyectoId;
    this.data.gastoProyecto.fechaCongreso = this.formGroup.controls.fechaCongreso.value;
    this.data.gastoProyecto.importeInscripcion = this.formGroup.controls.importeInscripcion.value;
    this.data.gastoProyecto.observaciones = this.formGroup.controls.observaciones.value;
    return this.data;
  }

  displayerProyecto(proyecto: IProyecto): string {
    return proyecto?.titulo;
  }

  private initProyectos(): void {
    if (this.data.proyectosSgiIds) {
      this.proyectos$ = from(this.data.proyectosSgiIds).pipe(
        mergeMap(proyectoId => this.proyectoService.findById(proyectoId)),
        toArray()
      )
    } else {
      this.proyectos$ = of([]);
    }

  }

  private setupI18N(): void {
    this.translate.get(
      IMPORTE_INSCRIPCION_KEY
    ).subscribe((value) => this.msgParamImporteInscripcion = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_KEY
    ).subscribe((value) => this.msgParamProyecto = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

}
