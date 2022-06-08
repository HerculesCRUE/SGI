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
import { DialogService } from '@core/services/dialog.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { BehaviorSubject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
import { ISolicitudProteccionListadoModalData, SolicitudProteccionListadoExportModalComponent } from '../../modals/solicitud-proteccion-listado-export-modal/solicitud-proteccion-listado-export-modal.component';
import { SolicitudProteccionFragment } from './solicitud-proteccion.fragment';

const MSG_CREATE = marker('btn.add.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SAVE_ERROR = marker('error.save.entity');
const MSG_UPDATE_ERROR = marker('error.update.entity');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.deactivate.entity.success');
const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');


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
      'activo',
      'acciones'],
    industrial: [
      'viaProteccion.nombre',
      'fechaFinPriorPresFasNacRec',
      'fechaPrioridadSolicitud',
      'numeroSolicitud',
      'numeroPublicacion',
      'numeroConcesion',
      'estado',
      'activo',
      'acciones'
    ]
  };

  msgParamEntity = {};

  textCrear: string;
  textCrearSuccess: string;
  textCrearError: string;
  textUpdateSuccess: string;
  textUpdateError: string;
  textDesactivar: string;
  textReactivar: string;
  textErrorDesactivar: string;
  textSuccessDesactivar: string;
  textSuccessReactivar: string;
  textErrorReactivar: string;

  public elementosPagina: number[];
  public totalElementos: number;

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
    private dialogService: DialogService
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
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textDesactivar = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textErrorDesactivar = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textSuccessDesactivar = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textReactivar = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textSuccessReactivar = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textErrorReactivar = value);
  }

  public getSuitableColumnsDef(tipoPropiedad: TipoPropiedad): string[] {
    if (!tipoPropiedad) {
      return undefined;
    }
    return tipoPropiedad === TipoPropiedad.INTELECTUAL ? this.displayedColumns.intelectual : this.displayedColumns.industrial;
  }

  /**
   * Desactivar Solicitud de Proteccion.
   * @param solicitudProteccion: ISolicitudProteccion
   */
  deactivateSolicitudProteccion(solicitudProteccion: ISolicitudProteccion, row: number): void {
    const subscription = this.dialogService.showConfirmation(this.textDesactivar)
      .subscribe((accept) => {
        if (accept && !this.formPart.deactivateSolicitudesProteccion.find(toDeactivate => toDeactivate === solicitudProteccion.id)) {
          const indexToActivate =
            this.formPart.activateSolicitudesProteccion.findIndex(toActivate => toActivate === solicitudProteccion.id);
          if (indexToActivate >= 0) {
            this.formPart.activateSolicitudesProteccion.splice(indexToActivate, 1);
            this.formPart.checkIfSaveChangesIsNeeded();
          } else {
            this.formPart.deactivateSolicitudesProteccion.push(solicitudProteccion.id);
            this.formPart.setChanges(true);
          }
          solicitudProteccion.activo = false;
        }
      });
    this.subscriptions.push(subscription);
  }

  /**
   * Activar un registro de Solicitud de Proteccion
   * @param solicitudProteccion: ISolicitudProteccion
   */
  activateSolicitudProteccion(solicitudProteccion: ISolicitudProteccion, row: number): void {
    const subscription = this.dialogService.showConfirmation(this.textReactivar)
      .subscribe((accept) => {
        if (accept && !this.formPart.activateSolicitudesProteccion.find(toDeactivate => toDeactivate === solicitudProteccion.id)) {
          const indexToDeactivate =
            this.formPart.deactivateSolicitudesProteccion.findIndex(toDeactivate => toDeactivate === solicitudProteccion.id);
          if (indexToDeactivate >= 0) {
            this.formPart.deactivateSolicitudesProteccion.splice(indexToDeactivate, 1);
            this.formPart.checkIfSaveChangesIsNeeded();
          } else {
            this.formPart.activateSolicitudesProteccion.push(solicitudProteccion.id);
            this.formPart.setChanges(true);
          }
          solicitudProteccion.activo = true;
        }
      });
    this.subscriptions.push(subscription);
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
      invencionId: this.formPart.getKey() as number
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
