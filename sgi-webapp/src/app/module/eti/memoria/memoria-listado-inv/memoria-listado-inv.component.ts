import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE, IComite } from '@core/models/eti/comite';
import { ESTADO_RETROSPECTIVA } from '@core/models/eti/estado-retrospectiva';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ESTADO_MEMORIA, TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
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
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';
import { MEMORIAS_ROUTE } from '../memoria-route-names';

const MSG_BUTTON_SAVE = marker('btn.add.entity');
const MSG_ERROR = marker('error.load');
const MSG_SUCCESS_ENVIAR_SECRETARIA = marker('msg.eti.memoria.enviar-secretaria.success');
const MSG_ERROR_ENVIAR_SECRETARIA = marker('error.eti.memoria.enviar-secretaria');
const MSG_CONFIRM_ENVIAR_SECRETARIA = marker('msg.eti.memoria.enviar-secretaria');
const MSG_CONFIRM_ELIMINAR = marker('msg.delete.entity');
const MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA = marker(
  'msg.eti.memoria.enviar-secretaria.retrospectiva.success');
const MSG_SUCCESS_ELIMINAR = marker('msg.delete.entity.success');
const MSG_ERROR_ELIMINAR = marker('error.delete.entity');
const MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('error.eti.memoria.enviar-secretaria.retrospectiva');
const MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('msg.eti.memoria.enviar-secretaria.retrospectiva');
const MEMORIA_KEY = marker('eti.memoria');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion');

@Component({
  selector: 'sgi-memoria-listado-inv',
  templateUrl: './memoria-listado-inv.component.html',
  styleUrls: ['./memoria-listado-inv.component.scss']
})
export class MemoriaListadoInvComponent extends AbstractTablePaginationComponent<IMemoriaPeticionEvaluacion> implements OnInit {
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

  textoDelete: string;
  textoDeleteSuccess: string;
  textoDeleteError: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    private readonly memoriaService: MemoriaService,
    protected readonly snackBarService: SnackBarService,
    protected readonly dialogService: DialogService,
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
      tipoEstadoMemoria: new FormControl('', [])
    });

    this.loadComites();
    this.loadEstadosMemoria();
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRM_ELIMINAR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_ELIMINAR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteSuccess = value);

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_ELIMINAR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteError = value);

    this.translate.get(
      PETICION_EVALUACION_KEY,
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

  protected createObservable(): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    const observable$ = this.memoriaService.findAllMemoriasEvaluacionByPersonaRef(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    this.displayedColumns = ['numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.numReferencia.value)
      .and('estadoActual.id', SgiRestFilterOperator.EQUALS, controls.tipoEstadoMemoria.value?.id?.toString());
  }

  protected loadTable(reset?: boolean) {
    // TODO Do the request with paginator/sort/filter values
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
    this.suscripciones.push(this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;
        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      }));
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

  hasPermisoEnviarSecretaria(estadoMemoriaId: number, responsable: boolean): boolean {
    // Si el estado es 'Completada', 'Favorable pendiente de modificaciones mínima',
    // 'Pendiente de correcciones', 'Completada seguimiento anual',
    // 'Completada seguimiento final' o 'En aclaracion seguimiento final' se muestra el botón de enviar.
    if ((estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA || estadoMemoriaId === ESTADO_MEMORIA.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
      || estadoMemoriaId === ESTADO_MEMORIA.PENDIENTE_CORRECCIONES || estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL
      || estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL
      || estadoMemoriaId === ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL) && !responsable) {
      return true;
    } else {
      return false;
    }
  }

  hasPermisoEliminar(estadoMemoriaId: number): boolean {
    // Si el estado es 'En elaboración' o 'Completada'.
    return (estadoMemoriaId === 1 || estadoMemoriaId === 2);
  }

  enviarSecretaria(memoria: IMemoriaPeticionEvaluacion) {
    const dialogSubscription = this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.enviarSecretaria(memoria.id);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA);
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA);
        }
      );
    this.suscripciones.push(dialogSubscription);
  }

  /**
   * Elimina la memoria.
   *
   * @param idMemoria Identificador de la memoria
   */
  delete(idMemoria: number): void {
    this.dialogService.showConfirmation(this.textoDelete)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.deleteById(idMemoria);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(this.textoDeleteSuccess);
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(this.textoDeleteError);
        }
      );
  }

  hasPermisoEnviarSecretariaRetrospectiva(memoria: IMemoria, responsable: boolean): boolean {
    // Si el estado es 'Completada', es de tipo CEEA y requiere retrospectiva se muestra el botón de enviar.
    // Si la retrospectiva ya está 'En secretaría' no se muestra el botón.
    // El estado de la memoria debe de ser mayor a FIN_EVALUACION
    return (memoria.estadoActual.id >= ESTADO_MEMORIA.FIN_EVALUACION && memoria.comite.id === COMITE.CEEA && memoria.requiereRetrospectiva
      && memoria.retrospectiva.estadoRetrospectiva.id === ESTADO_RETROSPECTIVA.COMPLETADA && !responsable);
  }

  enviarSecretariaRetrospectiva(memoria: IMemoriaPeticionEvaluacion) {
    this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.enviarSecretariaRetrospectiva(memoria.id);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA);
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA);
        }
      );
  }
}
