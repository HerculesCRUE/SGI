import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoSocioFragment } from './solicitud-proyecto-socio.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_DELETE_CASCADE = marker('msg.csp.solicitud-socio-colaborador.relations.delete');
const MSG_ERROR = marker('error.msg.csp.solicitud-socio-colaborador.relations.delete');
const SOLICITUD_PROYECTO_SOCIO_KEY = marker('csp.solicitud-proyecto-socio');

@Component({
  selector: 'sgi-solicitud-proyecto-socio',
  templateUrl: './solicitud-proyecto-socio.component.html',
  styleUrls: ['./solicitud-proyecto-socio.component.scss']
})
export class SolicitudProyectoSocioComponent extends FragmentComponent implements OnInit, OnDestroy {

  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoSocioFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['empresa', 'rolSocio', 'numInvestigadores', 'mesInicio', 'mesFin', 'importePresupuestado', 'importeSolicitado', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoSocio>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  msgParamEntities = {};
  textoDelete: string;

  constructor(
    public actionService: SolicitudActionService,
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.SOCIOS, actionService);
    this.formPart = this.fragment as SolicitudProyectoSocioFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.actionService.datosProyectoComplete$.pipe(
      take(1)
    ).subscribe(
      (complete) => {
        if (!complete) {
          this.router.navigate(['../', SOLICITUD_ROUTE_NAMES.PROYECTO_DATOS], { relativeTo: this.route });
        }
      }
    );

    const subscription = this.formPart.proyectoSocios$.subscribe(
      (proyectoSocios) => {
        this.dataSource.data = proyectoSocios;
      }
    );
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      switch (property) {
        case 'empresa':
          return wrapper.value.empresa.nombre;
        case 'rolSocio':
          return wrapper.value.rolSocio.nombre;
        default:
          return wrapper.value[property];
      }
    };
    this.subscriptions.push(subscription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntities = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  deleteProyectoSocio(wrapper: StatusWrapper<ISolicitudProyectoSocio>): void {
    this.subscriptions.push(
      this.solicitudProyectoSocioService.vinculaciones(wrapper.value.id).pipe(
        map(res => {
          return res ? MSG_DELETE_CASCADE : this.textoDelete;
        }),
        switchMap(message => {
          return this.dialogService.showConfirmation(message);
        }),
      ).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoSocio(wrapper);
          }
        },
        () => {
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }
}
