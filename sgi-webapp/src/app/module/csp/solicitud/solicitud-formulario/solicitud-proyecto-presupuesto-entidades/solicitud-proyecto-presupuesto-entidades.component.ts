import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoPresupuestoTotales } from '@core/models/csp/solicitud-proyecto-presupuesto-totales';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { SolicitiudPresupuestoModalComponent, SolicitudPresupuestoModalData } from '../../../shared/solicitud-presupuesto-modal/solicitud-presupuesto-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { EntidadFinanciadoraDesglosePresupuesto, SolicitudProyectoPresupuestoEntidadesFragment } from './solicitud-proyecto-presupuesto-entidades.fragment';

const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY = marker('csp.solicitud-entidad-financiadora');

interface IValoresCalculadosData {
  totalPresupuestadoUniversidad: number;
  totalSolicitadoUniversidad: number;
  totalPresupuestadoSocios: number;
  totalSolicitadoSocios: number;
  totalPresupuestado: number;
  totalSolicitado: number;
  totales: ISolicitudProyectoPresupuestoTotales;
}

@Component({
  selector: 'sgi-solicitud-proyecto-presupuesto-entidades',
  templateUrl: './solicitud-proyecto-presupuesto-entidades.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-entidades.component.scss']
})
export class SolicitudProyectoPresupuestoEntidadesComponent
  extends FormFragmentComponent<ISolicitudProyecto> implements OnInit, OnDestroy {
  SOLICITUD_ROUTE_NAMES = SOLICITUD_ROUTE_NAMES;
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoPresupuestoEntidadesFragment;

  private solicitudProyectoSocio$: Observable<ISolicitudProyectoSocio[]>;
  valoresCalculadosData = {} as IValoresCalculadosData;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = [
    'nombre',
    'cif',
    'ajena',
    'acciones'
  ];
  elementsPage = [5, 10, 25, 100];

  msgParamEntidadFinanciadorasEntity = {};

  dataSource = new MatTableDataSource<EntidadFinanciadoraDesglosePresupuesto>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    public actionService: SolicitudActionService,
    private router: Router,
    private route: ActivatedRoute,
    private matDialog: MatDialog,
    private readonly translate: TranslateService,
    private solicitudService: SolicitudService
  ) {
    super(actionService.FRAGMENT.DESGLOSE_PRESUPUESTO_ENTIDADES, actionService);
    this.formPart = this.fragment as SolicitudProyectoPresupuestoEntidadesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(33%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.solicitudProyectoSocio$ = this.solicitudService.findAllSolicitudProyectoSocio(this.formPart.getKey() as number).pipe(
      map((response) => response.items));
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

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor =
      (entidadFinanciadoraDesglose: EntidadFinanciadoraDesglosePresupuesto, property: string) => {
        switch (property) {
          case 'nombre':
            return entidadFinanciadoraDesglose.entidadFinanciadora.empresa.nombre;
          case 'cif':
            return entidadFinanciadoraDesglose.entidadFinanciadora.empresa.numeroIdentificacion;
          case 'ajena':
            return entidadFinanciadoraDesglose.ajena;
          default:
            return entidadFinanciadoraDesglose[property];
        }
      };

    const subscription = this.formPart.entidadesFinanciadoras$.subscribe(
      (entidadesFinanciadoras) => {
        this.dataSource.data = entidadesFinanciadoras;
        this.updateImportesTotales();
      }
    );
    this.subscriptions.push(subscription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntidadFinanciadorasEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showPresupuestoCompleto() {
    const data: SolicitudPresupuestoModalData = {
      idSolicitudProyecto: this.fragment.getKey() as number,
      presupuestos: [],
      global: false
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    this.matDialog.open(SolicitiudPresupuestoModalComponent, config);
  }

  private updateImportesTotales() {
    this.subscriptions.push(this.solicitudService.getSolicitudProyectoPresupuestoTotales(this.formPart.getKey() as number).
      subscribe(response => {
        this.valoresCalculadosData.totales = response;
        this.valoresCalculadosData.totalPresupuestadoUniversidad =
          response.importeTotalPresupuestadoAjeno + response.importeTotalPresupuestadoNoAjeno;
        this.valoresCalculadosData.totalSolicitadoUniversidad =
          response.importeTotalSolicitadoAjeno + response.importeTotalSolicitadoNoAjeno;

        this.solicitudProyectoSocio$.pipe(
          map(solicitudProyectoSocios => solicitudProyectoSocios.reduce(
            (total, solicitudProyectoSocio) => total + solicitudProyectoSocio.importePresupuestado, 0)
          )).subscribe(result => {
            this.valoresCalculadosData.totalPresupuestadoSocios = result;
            this.valoresCalculadosData.totalPresupuestado = this.valoresCalculadosData.totalPresupuestadoUniversidad + result;
          });

        this.solicitudProyectoSocio$.pipe(
          map(solicitudProyectoSocios => solicitudProyectoSocios.reduce(
            (total, solicitudProyectoSocio) => total + solicitudProyectoSocio.importeSolicitado, 0))
        ).subscribe(result => {
          this.valoresCalculadosData.totalSolicitadoSocios = result;
          this.valoresCalculadosData.totalSolicitado = this.valoresCalculadosData.totalSolicitadoUniversidad + result;
        });
      }));
  }
}
