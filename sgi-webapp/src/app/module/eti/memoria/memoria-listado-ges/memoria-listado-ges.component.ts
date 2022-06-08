import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ESTADO_MEMORIA_MAP } from '@core/models/eti/tipo-estado-memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { MEMORIAS_ROUTE } from '../memoria-route-names';
import { IMemoriaListadoModalData, MemoriaListadoExportModalComponent } from '../modals/memoria-listado-export-modal/memoria-listado-export-modal.component';

const MSG_BUTTON_SAVE = marker('btn.add.entity');
const MSG_ERROR = marker('error.load');
const MSG_ESTADO_ANTERIOR_OK = marker('msg.eti.memoria.estado-anterior.success');
const MSG_ESTADO_ANTERIOR_ERROR = marker('error.eti.memoria.estado-anterior');
const MSG_RECUPERAR_ESTADO = marker('msg.eti.memoria.estado-anterior');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion');

@Component({
  selector: 'sgi-memoria-listado-ges',
  templateUrl: './memoria-listado-ges.component.html',
  styleUrls: ['./memoria-listado-ges.component.scss']
})
export class MemoriaListadoGesComponent extends AbstractTablePaginationComponent<IMemoriaPeticionEvaluacion> implements OnInit {
  MEMORIAS_ROUTE = MEMORIAS_ROUTE;
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  textoCrear: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  memorias$: Observable<IMemoriaPeticionEvaluacion[]>;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  get ESTADO_MEMORIA_MAP() {
    return ESTADO_MEMORIA_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly memoriaService: MemoriaService,
    private readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private matDialog: MatDialog
  ) {

    super(snackBarService, MSG_ERROR);

    this.totalElementos = 0;

    this.suscripciones = [];

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
      titulo: new FormControl('', []),
      numReferencia: new FormControl('', []),
      tipoEstadoMemoria: new FormControl(null, []),
      solicitante: new FormControl('', []),
    });
  }

  private setupI18N(): void {
    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    const observable$ = this.memoriaService.findAll(this.getFindOptions(reset));
    // TODO: Eliminar casteo cuando se solucion la respuesta del back
    return observable$ as unknown as Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>>;
  }

  /**
   * Devuelve los datos rellenos de los responsabes de las memorias
   * @param memorias el listado de memorias
   * returns los responsables de memorias con todos sus datos
   */
  getDatosResponsablesMemorias(memorias: IMemoriaPeticionEvaluacion[]): IMemoriaPeticionEvaluacion[] {
    memorias.forEach((memoria) => {
      // cambiar en futuro pasando las referencias de las personas
      memoria = this.loadDatosResponsable(memoria);
    });
    return memorias;
  }

  /**
   * Devuelve los datos de persona de la memoria
   * @param memoria la memoria
   * returns la memoria con los datos del responsable
   */
  loadDatosResponsable(memoria: IMemoriaPeticionEvaluacion): IMemoriaPeticionEvaluacion {
    const personaServiceOneSubscription = this.personaService.findById(memoria.solicitante.id)
      .subscribe(
        (persona: IPersona) => {
          memoria.solicitante = persona;
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      );
    this.suscripciones.push(personaServiceOneSubscription);
    return memoria;
  }

  protected initColumns(): void {
    this.displayedColumns = ['nombre', 'numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.numReferencia.value)
      .and('estadoActual.id', SgiRestFilterOperator.EQUALS, controls.tipoEstadoMemoria.value?.toString())
      .and('personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);
  }

  protected loadTable(reset?: boolean) {
    this.memorias$ = this.getObservableLoadTable(reset).pipe(
      map((response) => {
        // Reset pagination to first page
        if (reset) {
          this.paginator.pageIndex = 0;
        }
        // Return the values
        return this.getDatosResponsablesMemorias(response as unknown as IMemoriaPeticionEvaluacion[]);
      }),
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.snackBarService.showError(MSG_ERROR);
        return of([]);
      })
    );
  }

  /**
   * Se recupera el estado anterior de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  private recuperarEstadoAnteriorMemoria(memoria: IMemoriaPeticionEvaluacion) {
    const recuperarEstadoSubscription = this.memoriaService.recuperarEstadoAnterior(memoria.id).pipe(
      map((response: IMemoria) => {
        if (response) {
          // Si todo ha salido bien se recarga la tabla con el cambio de estado actualizado y el aviso correspondiente
          this.snackBarService.showSuccess(MSG_ESTADO_ANTERIOR_OK);
          this.loadTable(true);
        } else {
          // Si no se puede cambiar de estado se muestra el aviso
          this.snackBarService.showError(MSG_ESTADO_ANTERIOR_ERROR);
        }
      })
    ).subscribe();

    this.suscripciones.push(recuperarEstadoSubscription);
  }

  /**
   * Confirmación para recuperar el estado de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  public recuperarEstadoAnterior(memoria: IMemoriaPeticionEvaluacion) {
    const dialogServiceSubscription = this.dialogService.showConfirmation(MSG_RECUPERAR_ESTADO).pipe(
      map((aceptado: boolean) => {
        if (aceptado) {
          this.recuperarEstadoAnteriorMemoria(memoria);
        }
      })
    ).subscribe();

    this.suscripciones.push(dialogServiceSubscription);
  }

  public openExportModal() {
    const data: IMemoriaListadoModalData = {
      findOptions: this.findOptions,
      isInvestigador: false
    };

    const config = {
      data
    };
    this.matDialog.open(MemoriaListadoExportModalComponent, config);
  }
}
