import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { TipoEstadoMemoriaService } from '@core/services/eti/tipo-estado-memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { Observable } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';
import { MEMORIAS_ROUTE } from '../memoria-route-names';

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

  comiteListado: IComite[];
  filteredComites: Observable<IComite[]>;

  estadoMemoriaListado: TipoEstadoMemoria[];
  filteredEstadosMemoria: Observable<TipoEstadoMemoria[]>;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    private readonly dialogService: DialogService,
    private readonly memoriaService: MemoriaService,
    private readonly translate: TranslateService
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
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      numReferencia: new FormControl('', []),
      tipoEstadoMemoria: new FormControl('', []),
      solicitante: new FormControl('', []),
    });

    this.loadComites();
    this.loadEstadosMemoria();
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

  protected createObservable(): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    const observable$ = this.memoriaService.findAll(this.getFindOptions());
    // TODO: Eliminar casteo cuando se solucion la respuesta del back
    return observable$ as unknown as Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>>;
  }
  protected initColumns(): void {
    this.displayedColumns = ['numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.numReferencia.value)
      .and('estadoActual.id', SgiRestFilterOperator.EQUALS, controls.tipoEstadoMemoria.value?.id?.toString())
      .and('personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);
  }

  protected loadTable(reset?: boolean) {
    this.memorias$ = this.getObservableLoadTable(reset);
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre de un estado memoria.
   * @param tipoEstadoMemoria tipo estado memoria
   * returns nombre estadoMemoria
   */
  getEstadoMemoria(tipoEstadoMemoria: TipoEstadoMemoria): string {
    return tipoEstadoMemoria?.nombre;
  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  loadComites(): void {
    const comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      });

    this.suscripciones.push(comitesSubscription);
  }

  /**
   * Recupera un listado de los estados memoria que hay en el sistema.
   */
  loadEstadosMemoria(): void {
    const estadosMemoriaSubscription = this.tipoEstadoMemoriaService.findAll().subscribe(
      (response) => {
        this.estadoMemoriaListado = response.items;
        this.filteredEstadosMemoria = this.formGroup.controls.tipoEstadoMemoria.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterEstadoMemoria(value))
          );
      });
    this.suscripciones.push(estadosMemoriaSubscription);
  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | IComite): IComite[] {
    let filterValue: string;
    if (value === null) {
      value = '';
    }
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comiteListado.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable estado memoria.
   * @param value value a filtrar (string o nombre estado memoria).
   * @returns lista de estados memoria filtrados.
   */
  private filterEstadoMemoria(value: string | TipoEstadoMemoria): TipoEstadoMemoria[] {
    let filterValue: string;
    if (value === null) {
      value = '';
    }
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.estadoMemoriaListado.filter
      (estadoMemoria => estadoMemoria.nombre.toLowerCase().includes(filterValue));
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
}
