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
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { PartidaGastoDataModal, PartidaGastoModalComponent } from '../../../shared/partida-gasto-modal/partida-gasto-modal.component';
import { SolicitiudPresupuestoModalComponent, SolicitudPresupuestoModalData } from '../../../shared/solicitud-presupuesto-modal/solicitud-presupuesto-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoPresupuestoGlobalFragment } from './solicitud-proyecto-presupuesto-global.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_PRESUPUESTO_GLOBAL_PARTIDA_GASTO_KEY = marker('csp.partida-gasto');
const SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_GASTO_KEY = marker('csp.solicitud-desglose-presupuesto.global.partidas-gasto');



@Component({
  selector: 'sgi-solicitud-proyecto-presupuesto-global',
  templateUrl: './solicitud-proyecto-presupuesto-global.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-global.component.scss']
})

export class SolicitudProyectoPresupuestoGlobalComponent extends FormFragmentComponent<ISolicitudProyecto> implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoPresupuestoGlobalFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = [
    'conceptoGasto',
    'anualidad',
    'importePresupuestado',
    'importeSolicitado',
    'observaciones',
    'acciones'
  ];
  elementsPage = [5, 10, 25, 100];

  msgParamPartidaGastoEntity = {};
  msgParamEntity = {};
  textoDelete: string;

  private solicitudProyectoSocio$: Observable<ISolicitudProyectoSocio[]>;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoPresupuesto>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private actionService: SolicitudActionService,
    private router: Router,
    private route: ActivatedRoute,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private solicitudService: SolicitudService
  ) {
    super(actionService.FRAGMENT.DESGLOSE_PRESUPUESTO_GLOBAL, actionService);
    this.formPart = this.fragment as SolicitudProyectoPresupuestoGlobalFragment;

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
      (partidaGasto: StatusWrapper<ISolicitudProyectoPresupuesto>, property: string) => {
        switch (property) {
          case 'conceptoGasto':
            return partidaGasto.value.conceptoGasto?.nombre;
          case 'anualidad':
            return partidaGasto.value.anualidad;
          case 'importe':
            return partidaGasto.value.importeSolicitado;
          case 'observaciones':
            return partidaGasto.value.observaciones;
          default:
            return partidaGasto[property];
        }
      };

    const subcription = this.formPart.partidasGastos$
      .subscribe((partidasGasto) => {
        this.dataSource.data = partidasGasto;
        this.updateImportesTotales();
      });
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_GLOBAL_PARTIDA_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPartidaGastoEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_GLOBAL_PARTIDA_GASTO_KEY,
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

  deletePartidaGasto(wrapper: StatusWrapper<ISolicitudProyectoPresupuesto>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePartidaGasto(wrapper);
          }
        }
      )
    );
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoPresupuesto>): void {
    const data: PartidaGastoDataModal = {
      partidaGasto: wrapper ? wrapper.value : {} as ISolicitudProyectoPresupuesto,
      convocatoriaId: this.actionService.convocatoriaId,
      readonly: this.formPart.readonly
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };

    const dialogRef = this.matDialog.open(PartidaGastoModalComponent, config);
    dialogRef.afterClosed().subscribe((partidaGasto) => {
      if (partidaGasto) {
        if (!wrapper) {
          this.formPart.addPartidaGasto(partidaGasto);
        } else if (!wrapper.created) {
          const wrapperUpdated = new StatusWrapper<ISolicitudProyectoPresupuesto>(wrapper.value);
          this.formPart.updatePartidaGasto(wrapperUpdated);
        }
        this.updateImportesTotales();
      }
    });
  }

  private updateImportesTotales() {
    /* Presupuestado por Universidad Sin Costes Indirectos */
    const importePresupuestadoUniversidad = this.formPart.partidasGastos$.value
      .filter(partidaGasto => !partidaGasto.value.conceptoGasto.costesIndirectos)
      .reduce((total, partidaGasto) => total + partidaGasto.value.importePresupuestado, 0);
    this.formPart.valoresCalculadosData.importePresupuestadoUniversidad = importePresupuestadoUniversidad;
    /* Presupuestado por Universidad Con Costes Indirectos */
    const importePresupuestadoUniversidadCostesIndirectos = this.formPart.partidasGastos$.value
      .filter(partidaGasto => partidaGasto.value.conceptoGasto.costesIndirectos)
      .reduce((total, partidaGasto) => total + partidaGasto.value.importePresupuestado, 0);
    this.formPart.valoresCalculadosData.importePresupuestadoUniversidadCostesIndirectos = importePresupuestadoUniversidadCostesIndirectos;
    /* Total Presupuestado por Universidad */
    const totalImportePresupuestadoUniversidad = this.formPart.partidasGastos$.value.reduce(
      (total, partidaGasto) => total + partidaGasto.value.importePresupuestado, 0);
    this.formPart.valoresCalculadosData.totalImportePresupuestadoUniversidad = totalImportePresupuestadoUniversidad;

    /* Solicitado por Universidad Sin Costes Indirectos */
    const importeSolicitadoUniversidad = this.formPart.partidasGastos$.value
      .filter(partidaGasto => !partidaGasto.value.conceptoGasto.costesIndirectos)
      .reduce((total, partidaGasto) => total + partidaGasto.value.importeSolicitado, 0);
    this.formPart.valoresCalculadosData.importeSolicitadoUniversidad = importeSolicitadoUniversidad;
    /* Solicitado por Universidad Con Costes Indirectos */
    const importeSolicitadoUniversidadCostesIndirectos = this.formPart.partidasGastos$.value
      .filter(partidaGasto => partidaGasto.value.conceptoGasto.costesIndirectos)
      .reduce((total, partidaGasto) => total + partidaGasto.value.importeSolicitado, 0);
    this.formPart.valoresCalculadosData.importeSolicitadoUniversidadCostesIndirectos = importeSolicitadoUniversidadCostesIndirectos;
    /* Total Solicitado por Universidad*/
    const totalSolicitadoUniversidad = this.formPart.partidasGastos$.value.reduce(
      (total, partidaGasto) => total + partidaGasto.value.importeSolicitado, 0);
    this.formPart.valoresCalculadosData.totalImporteSolicitadoUniversidad = totalSolicitadoUniversidad;

    this.solicitudProyectoSocio$.pipe(
      map(solicitudProyectoSocios => solicitudProyectoSocios.reduce(
        (total, solicitudProyectoSocio) => total + solicitudProyectoSocio.importePresupuestado, 0)
      )).subscribe(result => {
        this.formPart.valoresCalculadosData.totalPresupuestadoSocios = result;
        this.formPart.valoresCalculadosData.totalPresupuestado = totalImportePresupuestadoUniversidad + result;
      });

    this.solicitudProyectoSocio$.pipe(
      map(solicitudProyectoSocios => solicitudProyectoSocios.reduce(
        (total, solicitudProyectoSocio) => total + solicitudProyectoSocio.importeSolicitado, 0))
    ).subscribe(result => {
      this.formPart.valoresCalculadosData.totalSolicitadoSocios = result;
      this.formPart.valoresCalculadosData.totalSolicitado = totalSolicitadoUniversidad + result;
    });
  }

  showPresupuestoCompleto() {
    const data: SolicitudPresupuestoModalData = {
      idSolicitudProyecto: this.fragment.getKey() as number,
      presupuestos: this.dataSource.data.map(wrapper => wrapper.value),
      global: true
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    this.matDialog.open(SolicitiudPresupuestoModalComponent, config);
  }
}
