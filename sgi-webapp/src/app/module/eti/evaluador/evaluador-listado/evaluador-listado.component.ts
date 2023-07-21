import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { EvaluadorListadoExportModalComponent } from '../modals/evaluador-listado-export-modal/evaluador-listado-export-modal.component';

const MSG_BUTTON_SAVE = marker('btn.add.entity');
const MSG_ERROR = marker('error.load');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_SUCCESS = marker('msg.delete.entity.success');
const EVALUADOR_KEY = marker('eti.evaluador');

@Component({
  selector: 'sgi-evaluador-listado',
  templateUrl: './evaluador-listado.component.html',
  styleUrls: ['./evaluador-listado.component.scss']
})

export class EvaluadorListadoComponent extends AbstractTablePaginationComponent<IEvaluador> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluadores$: Observable<IEvaluador[]> = of();

  textoCrear: string;
  textoDelete: string;
  textoDeleteSuccess: string;

  personasRef: string[];

  personas$: Observable<IPersona[]> = of();

  private limiteRegistrosExportacionExcel: string;

  get tipoColectivoEvaluador() {
    return TipoColectivo.EVALUADOR_ETICA;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluadoresService: EvaluadorService,
    protected readonly snackBarService: SnackBarService,
    private readonly personaService: PersonaService,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      estado: new FormControl('', []),
      solicitante: new FormControl('', [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-evaluador-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      EVALUADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      EVALUADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteSuccess = value);

    this.translate.get(
      EVALUADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluador>> {
    return this.evaluadoresService.findAll(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.displayedColumns = ['nombre', 'persona', 'comite', 'cargoComite', 'fechaAlta', 'fechaBaja', 'estado', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString());
    if (controls.estado.value) {
      // TODO: Revisar lógica
      filter
        .and(
          new RSQLSgiRestFilter(
            new RSQLSgiRestFilter(
              'fechaBaja', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(DateTime.now())
            ).and('fechaAlta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(DateTime.now().plus({ days: 1 })))
          ).or(
            new RSQLSgiRestFilter('fechaAlta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(DateTime.now().plus({ days: 1 })))
              .and('fechaBaja', SgiRestFilterOperator.IS_NULL, '')
          )
        );
    }
    filter.and('personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);

    return filter;
  }

  protected loadTable(reset?: boolean) {
    // Do the request with paginator/sort/filter values
    this.evaluadores$ = this.createObservable().pipe(
      map((response) => {
        // Map response total
        this.totalElementos = response.total;
        // Reset pagination to first page
        if (reset) {
          this.paginator.pageIndex = 0;
        }
        // Return the values
        return this.getDatosEvaluadores(response.items);
      }),
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        this.processError(error);
        return of([]);
      })
    );
  }

  /**
   * Devuelve los datos rellenos de evaluadores
   * @param evaluadores el listado de evaluadores
   * returns los evaluadores con todos sus datos
   */
  getDatosEvaluadores(evaluadores: IEvaluador[]): IEvaluador[] {
    this.personasRef = [];
    evaluadores.forEach((evaluador) => {

      if ((evaluador.fechaAlta != null && evaluador.fechaAlta <= DateTime.now()) &&
        ((evaluador.fechaBaja != null && evaluador.fechaBaja >= DateTime.now()) || (evaluador.fechaBaja == null))) {
        evaluador.activo = true;
      } else {
        evaluador.activo = false;
      }

      if (evaluador.persona.id && evaluador.persona.id !== '') {
        this.personasRef.push(evaluador.persona.id);
      }

      // cambiar en futuro pasando las referencias de las personas
      evaluador = this.loadDatosUsuario(evaluador);
    });
    return evaluadores;
  }

  /**
   * Devuelve los datos de persona del evaluador
   * @param evaluador el evaluador
   * returns el evaluador con los datos de persona
   */
  loadDatosUsuario(evaluador: IEvaluador): IEvaluador {
    const personaServiceOneSubscription = this.personaService.findById(evaluador.persona.id)
      .subscribe(
        (persona: IPersona) => {
          evaluador.persona = persona;
        },
        (error) => {
          this.logger.error(error);
          this.processError(error);
        }
      );
    this.suscripciones.push(personaServiceOneSubscription);
    return evaluador;
  }

  /**
   * Devuelve si el estado es activo o inactivo.
   * @param estado estado activo/inactivo
   * returns activo o inactivo
   */
  getEstado(estado: boolean): boolean {
    return estado;
  }

  /**
   * Elimina el evaluador con el id recibido por parametro.
   * @param evaluadorId id evaluador
   * @param event evento lanzado
   */
  borrar(evaluadorId: number, $event: Event): void {
    $event.stopPropagation();
    $event.preventDefault();

    const dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(this.textoDelete).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          const evaluadorServiceDeleteSubscription = this.evaluadoresService
            .deleteById(evaluadorId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(this.textoDeleteSuccess);
            },
              this.processError
            );
          this.suscripciones.push(evaluadorServiceDeleteSubscription);
        }
        aceptado = false;
      });
    this.suscripciones.push(dialogServiceSubscriptionGetSubscription);
  }

  public openExportModal() {
    const data: IBaseExportModalData = {
      findOptions: this.findOptions,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(EvaluadorListadoExportModalComponent, config);
  }
}
