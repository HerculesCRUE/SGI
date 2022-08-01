import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from '../../ejecucion-economica-route-names';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { formatRequerimientoJustificacionNombre } from '../../pipes/requerimiento-justificacion-nombre.pipe';
import { SeguimientoJustificacionRequerimientosFragment } from './seguimiento-justificacion-requerimientos.fragment';

const MSG_DELETE_REQUERIMIENTO_JUSTIFICACION = marker('msg.delete.csp.requerimiento-justificacion');
const REQUERIMIENTO_JUSTIFICACION_KEY = marker('csp.requerimiento-justificacion');

@Component({
  selector: 'sgi-seguimiento-justificacion-requerimientos',
  templateUrl: './seguimiento-justificacion-requerimientos.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimientos.component.scss']
})
export class SeguimientoJustificacionRequerimientosComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: SeguimientoJustificacionRequerimientosFragment;

  displayedColumns = [
    'numRequerimiento', 'proyectoProyectoSge.proyecto.id', 'proyectoPeriodoJustificacion.identificadorJustificacion',
    'tipoRequerimiento.nombre', 'requerimientoPrevio', 'fechaNotificacion', 'fechaFinAlegacion', 'importeAceptado',
    'importeRechazado', 'importeReintegrar', 'acciones'
  ];

  msgParamEntity = {};

  dataSource = new MatTableDataSource<StatusWrapper<IRequerimientoJustificacion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get ROUTE_NAMES() {
    return ROUTE_NAMES;
  }

  get EJECUCION_ECONOMICA_ROUTE_NAMES() {
    return EJECUCION_ECONOMICA_ROUTE_NAMES;
  }

  constructor(
    public actionService: EjecucionEconomicaActionService,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionRequerimientosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(this.formPart?.getRequerimientosJustificacion$().subscribe(
      (requerimientosJustificacion) => {
        this.dataSource.data = requerimientosJustificacion;
      }
    ));

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (requerimiento: StatusWrapper<IRequerimientoJustificacion>, property: string) => {
        switch (property) {
          case 'proyectoProyectoSge.proyecto.id':
            return requerimiento.value.proyectoProyectoSge?.proyecto?.id;
          case 'proyectoPeriodoJustificacion.identificadorJustificacion':
            return requerimiento.value.proyectoPeriodoJustificacion?.identificadorJustificacion;
          case 'tipoRequerimiento.nombre':
            return requerimiento.value.tipoRequerimiento?.nombre;
          case 'requerimientoPrevio':
            return formatRequerimientoJustificacionNombre(requerimiento.value.requerimientoPrevio);
          default:
            return requerimiento.value[property];
        }
      };
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  isRequerimientoDeleteable(requerimientoToCheck: StatusWrapper<IRequerimientoJustificacion>): boolean {
    return !this.dataSource.data.some(requerimiento => requerimiento.value?.requerimientoPrevio?.id === requerimientoToCheck.value.id);
  }

  deleteRequerimiento(requerimientoToDelete: StatusWrapper<IRequerimientoJustificacion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE_REQUERIMIENTO_JUSTIFICACION).subscribe(aceptado => {
        if (aceptado) {
          this.formPart.deleteRequerimiento(requerimientoToDelete);
        }
      }
      )
    );
  }

  private setupI18N(): void {
    this.translate.get(
      REQUERIMIENTO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }
}
