import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
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
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
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

  constructor(
    public actionService: InvencionActionService,
    private snackBarService: SnackBarService,
    private translate: TranslateService,
    private matDialog: MatDialog,
    private solicitudProteccionService: SolicitudProteccionService,
    private logger: NGXLogger,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.SOLICITUDES_PROTECCION, actionService);
    this.formPart = this.fragment as SolicitudProteccionFragment;
    this.elementosPagina = [5, 10, 25, 100];
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

}
