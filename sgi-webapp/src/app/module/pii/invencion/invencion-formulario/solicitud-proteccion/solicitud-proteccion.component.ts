import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { BehaviorSubject, Subscription, forkJoin, of } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
import { ISolicitudProteccionListadoModalData, SolicitudProteccionListadoExportModalComponent } from '../../modals/solicitud-proteccion-listado-export-modal/solicitud-proteccion-listado-export-modal.component';
import { SolicitudProteccionFragment } from './solicitud-proteccion.fragment';

const MSG_CREATE = marker('btn.add.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SAVE_ERROR = marker('error.save.entity');
const MSG_UPDATE_ERROR = marker('error.update.entity');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');
const SOLICITUD_PROTECCION_NO_DELETABLE = marker('pii.solicitud-proteccion.no-deletable');
const INVENCION_GASTO_KEY = marker('pii.invencion-gastos');
const PAIESES_VALIDADOS_KEY = marker('pii.solicitud-proteccion.pais-validado.titulo');
const PROCEDIMIENTOS_KEY = marker('pii.solicitud-proteccion.procedimiento');
const MSG_AND = marker('msg.and');


@Component({
  selector: 'sgi-solicitud-proteccion',
  templateUrl: './solicitud-proteccion.component.html',
  styleUrls: ['./solicitud-proteccion.component.scss']
})
export class SolicitudProteccionComponent extends FragmentComponent implements OnInit, OnDestroy, AfterViewInit {

  private subscriptions: Subscription[] = [];
  public ROUTE_NAMES = ROUTE_NAMES;

  public formPart: SolicitudProteccionFragment;

  public fxFlexProperties: FxFlexProperties;
  public fxLayoutProperties: FxLayoutProperties;

  formGroup: FormGroup;

  private readonly displayedColumns = {
    intelectual: [
      'viaProteccion.nombre',
      'fechaPrioridadSolicitud',
      'numeroSolicitud',
      'numeroRegistro',
      'acciones'],
    industrial: [
      'viaProteccion.nombre',
      'fechaFinPriorPresFasNacRec',
      'fechaPrioridadSolicitud',
      'numeroSolicitud',
      'numeroPublicacion',
      'numeroConcesion',
      'estado',
      'acciones'
    ]
  };

  msgParamEntity = {};

  textCrear: string;
  textCrearSuccess: string;
  textCrearError: string;
  textUpdateSuccess: string;
  textUpdateError: string;
  msgConfirmDelete: string;
  msgNoDeletable: string;
  textErrorDelete: string;
  textSuccessDelete: string;

  public elementosPagina: number[];
  public totalElementos: number;

  private limiteRegistrosExportacionExcel: string;

  public dataSource = new MatTableDataSource<ISolicitudProteccion>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  public showPaisSelector = new BehaviorSubject<boolean>(false);
  findOptions: SgiRestFindOptions;

  constructor(
    public actionService: InvencionActionService,
    private translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly cnfService: ConfigService,
    private solicitudProteccionService: SolicitudProteccionService
  ) {
    super(actionService.FRAGMENT.SOLICITUDES_PROTECCION, actionService);
    this.formPart = this.fragment as SolicitudProteccionFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.setupLayout();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    const subscription = this.formPart.solicitudesProteccion$.subscribe(
      (solicitudesProteccion) => {
        this.dataSource.data = solicitudesProteccion.map(wrapper => wrapper.value);
      }
    );
    this.subscriptions.push(subscription);
    this.dataSource.paginator = this.paginator;
    this.formGroup = this.setupSearchFormGroup();
    this.formGroup.controls.viaProteccion.valueChanges.subscribe(via => this.showPaisSelector.next(via?.paisEspecifico));

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('pii-exp-max-num-registros-excel-solicitud-proteccion').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  ngAfterViewInit() {
    this.dataSource.sortingDataAccessor = (item: ISolicitudProteccion, property: string) => {
      switch (property) {
        case 'viaProteccion.nombre': return item.viaProteccion.nombre;
        default: return item[property];
      }
    };
    this.dataSource.sort = this.sort;
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CREATE,
          { entity: value, ...this.msgParamEntity }
        );
      })
    ).subscribe((value) => this.textCrear = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textCrearSuccess = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textCrearError = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textUpdateSuccess = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textUpdateError = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.msgConfirmDelete = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textErrorDelete = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textSuccessDelete = value);

  }

  public getSuitableColumnsDef(tipoPropiedad: TipoPropiedad): string[] {
    if (!tipoPropiedad) {
      return undefined;
    }
    return tipoPropiedad === TipoPropiedad.INTELECTUAL ? this.displayedColumns.intelectual : this.displayedColumns.industrial;
  }

  /**
   * Eliminar Solicitud de Proteccion.
   * @param solicitudProteccion: ISolicitudProteccion
   */
  deleteSolicitudProteccion(solicitudProteccion: ISolicitudProteccion): void {
    let eliminable$ = of(true);
    if (solicitudProteccion?.id) {
      eliminable$ = forkJoin({
        hasInvencionGastos: this.solicitudProteccionService.hasInvencionGastos(solicitudProteccion.id),
        hasPaisesValidados: this.solicitudProteccionService.hasPaisesValidados(solicitudProteccion.id),
        hasProcedimientos: this.solicitudProteccionService.hasProcedimientos(solicitudProteccion.id)
      }).pipe(
        switchMap(({ hasInvencionGastos, hasPaisesValidados, hasProcedimientos }) => {
          const isEliminable = !hasInvencionGastos && !hasPaisesValidados && !hasProcedimientos
          if (!isEliminable) {
            const relaciones = [];
            if (hasInvencionGastos) {
              relaciones.push(this.translate.instant(INVENCION_GASTO_KEY));
            }

            if (hasPaisesValidados) {
              relaciones.push(this.translate.instant(PAIESES_VALIDADOS_KEY, MSG_PARAMS.CARDINALIRY.PLURAL));
            }

            if (hasProcedimientos) {
              relaciones.push(this.translate.instant(PROCEDIMIENTOS_KEY, MSG_PARAMS.CARDINALIRY.PLURAL));
            }

            let relacionesMsg = '';
            if (relaciones.length > 2) {
              const lastRelacion = relaciones.pop();
              relacionesMsg = `${relaciones.join(', ')} ${this.translate.instant(MSG_AND)} ${lastRelacion}`;
            } else {
              relacionesMsg = relaciones.join(` ${this.translate.instant(MSG_AND)} `);
            }

            return this.dialogService.showInfoDialog(SOLICITUD_PROTECCION_NO_DELETABLE, { entities: relacionesMsg });
          }

          return this.dialogService.showConfirmation(this.msgConfirmDelete).pipe(
            filter(accepted => !!accepted),
            tap(() => this.formPart.deleteSolicitudProteccion(solicitudProteccion))
          )

        })
      );
    }

    this.subscriptions.push(
      eliminable$.subscribe()
    );
  }

  private setupLayout(): void {
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

  public onSearch(): void {
    this.formPart.loadTable(this.createFilterOptions());
  }

  public onClearFilters(): void {
    this.formGroup.reset();
  }

  private setupSearchFormGroup(): FormGroup {
    return new FormGroup({
      numeroSolicitud: new FormControl(null),
      fechaSolicitudDesde: new FormControl(null),
      fechaSolicitudHasta: new FormControl(null),
      viaProteccion: new FormControl(null),
      pais: new FormControl(null),
      fechaFinPrioridadDesde: new FormControl(null),
      fechaFinPrioridadHasta: new FormControl(null),
      estado: new FormControl(null),
      titulo: new FormControl(null)
    });
  }

  private createFilterOptions(): SgiRestFindOptions {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('numeroSolicitud', SgiRestFilterOperator.EQUALS, controls.numeroSolicitud?.value?.toString())
      .and('fechaPrioridadSolicitud', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaSolicitudDesde?.value))
      .and('fechaPrioridadSolicitud', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaSolicitudHasta?.value))
      .and('fechaFinPriorPresFasNacRec', SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaFinPrioridadDesde?.value))
      .and('fechaFinPriorPresFasNacRec', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaFinPrioridadHasta?.value))
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo?.value?.toString())
      .and('viaProteccion.id', SgiRestFilterOperator.EQUALS, controls.viaProteccion.value?.id?.toString())
      .and('paisProteccionRef', SgiRestFilterOperator.EQUALS, controls.pais.value?.id?.toString())
      .and('estado', SgiRestFilterOperator.EQUALS, controls.estado?.value?.toString());

    return this.getFindOptions(filter, true);
  }

  public openExportModal(): void {
    const data: ISolicitudProteccionListadoModalData = {
      findOptions: this.createFilterOptions(),
      invencionId: this.formPart.getKey() as number,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(SolicitudProteccionListadoExportModalComponent, config);
  }

  /**
   * Crea las opciones para el listado que devuelve el servidor.
   * Hay que añadirlo al método del servicio que llamamos
   *
   * @param reset Indica la pagina actual es la primera o no
   */
  private getFindOptions(filter: SgiRestFilter, reset?: boolean): SgiRestFindOptions {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator?.pageIndex,
        size: this.paginator?.pageSize,
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter,
    };
    this.findOptions = options;
    return options;
  }
}
