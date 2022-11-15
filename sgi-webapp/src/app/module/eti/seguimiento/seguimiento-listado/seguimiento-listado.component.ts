import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISeguimientoListadoModalData, SeguimientoListadoExportModalComponent } from '../modals/seguimiento-listado-export-modal/seguimiento-listado-export-modal.component';
import { RolPersona } from '../seguimiento-listado-export.service';

@Component({
  selector: 'sgi-seguimiento-listado',
  templateUrl: './seguimiento-listado.component.html',
  styleUrls: ['./seguimiento-listado.component.scss']
})
export class SeguimientoListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {
  evaluaciones: IEvaluacion[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    private readonly personaService: PersonaService,
    private readonly evaluadorService: EvaluadorService,
    private matDialog: MatDialog
  ) {
    super();
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl(null),
      fechaEvaluacionInicio: new FormControl(null),
      fechaEvaluacionFin: new FormControl(null),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl(''),
      tipoEvaluacion: new FormControl(null)
    });
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    const observable$ = this.evaluadorService.getSeguimientos(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['memoria.comite.comite', 'tipoEvaluacion.nombre', 'memoria.tipoMemoria.nombre', 'convocatoriaReunion.fechaEvaluacion',
      'memoria.numReferencia', 'solicitante', 'version', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    const evaluaciones$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      evaluaciones$.subscribe(
        (evaluaciones) => {
          this.evaluaciones = [];
          if (evaluaciones) {
            this.evaluaciones = evaluaciones;
            this.loadSolicitantes();
          }
        },
        (error) => {
          this.processError(error);
        })
    );
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitantes(): void {
    this.evaluaciones.forEach((evaluacion) => {
      const personaRef = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
      if (personaRef) {
        this.suscripciones.push(
          this.personaService.findById(personaRef).subscribe(
            (persona: IPersona) => evaluacion.memoria.peticionEvaluacion.solicitante = persona,
            (error) => {
              this.processError(error);
            }
          )
        );
      }
    });
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and(
        'convocatoriaReunion.fechaEvaluacion',
        SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value)
      ).and(
        'convocatoriaReunion.fechaEvaluacion',
        SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaEvaluacionFin.value)
      ).and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.memoriaNumReferencia.value)
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString());

    return filter;
  }

  public openExportModal() {
    const data: ISeguimientoListadoModalData = {
      findOptions: this.findOptions,
      rolPersona: RolPersona.EVALUADOR
    };

    const config = {
      data
    };
    this.matDialog.open(SeguimientoListadoExportModalComponent, config);
  }
}
