import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IdentificadorJustificacionModalComponent, IdentificadorJustificacionModalData } from '../../modals/identificador-justificacion-modal/identificador-justificacion-modal.component';
import {
  IProyectoPeriodoJustificacionWithTituloProyecto, IProyectoSeguimientoEjecucionEconomicaData, SeguimientoJustificacionResumenFragment
} from './seguimiento-justificacion-resumen.fragment';

@Component({
  selector: 'sgi-seguimiento-justificacion-resumen',
  templateUrl: './seguimiento-justificacion-resumen.component.html',
  styleUrls: ['./seguimiento-justificacion-resumen.component.scss']
})
export class SeguimientoJustificacionResumenComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SeguimientoJustificacionResumenFragment;

  proyectosSGIElementosPagina = [5, 10, 25, 100];
  proyectosSGIDisplayedColumns = [
    'proyectoSgeRef',
    'proyectoId',
    'codigoExterno',
    'nombre',
    'responsables',
    'fechaInicio',
    'fechaFin',
    'tituloConvocatoria',
    'entidadesFinanciadoras',
    'importeConcedido',
    'importeConcedidoCostesIndirectos',
    'acciones',
  ];

  calendarioJustificacionElementosPagina = [5, 10, 25, 100];
  calendarioJustificacionDisplayedColumns = [
    'proyecto.id',
    'tituloProyecto',
    'numPeriodo',
    'fechaInicio',
    'fechaFin',
    'fechaInicioPresentacion',
    'fechaFinPresentacion',
    'tipoJustificacion',
    'identificadorJustificacion',
    'fechaPresentacionJustificacion',
    'acciones',
  ];

  proyectosSGIDataSource = new MatTableDataSource<IProyectoSeguimientoEjecucionEconomicaData>();
  calendarioJustificacionDataSource = new MatTableDataSource<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>>();
  @ViewChild('proyectosSGIPaginator', { static: true }) proyectosSGIPaginator: MatPaginator;
  @ViewChild('proyectosSGISort', { static: true }) proyectosSGISort: MatSort;
  @ViewChild('calendarioJustificacionPaginator', { static: true }) calendarioJustificacionPaginator: MatPaginator;
  @ViewChild('calendarioJustificacionSort', { static: true }) calendarioJustificacionSort: MatSort;

  get CSP_ROUTE_NAMES() {
    return CSP_ROUTE_NAMES;
  }

  get TIPO_JUSTIFICACION_MAP() {
    return TIPO_JUSTIFICACION_MAP;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private matDialog: MatDialog) {
    super(actionService.FRAGMENT.SEGUIMIENTO_JUSTIFICACION_RESUMEN, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionResumenFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.proyectosSGIDataSource.paginator = this.proyectosSGIPaginator;
    this.proyectosSGIDataSource.sort = this.proyectosSGISort;

    this.calendarioJustificacionDataSource.paginator = this.calendarioJustificacionPaginator;
    this.calendarioJustificacionDataSource.sort = this.calendarioJustificacionSort;

    this.subscriptions.push(this.formPart.getProyectosSGI$().subscribe(elements => {
      this.proyectosSGIDataSource.data = elements;
    }));
    this.subscriptions.push(this.formPart.getPeriodosJustificacion$().subscribe(elements => {
      this.calendarioJustificacionDataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>): void {
    const data: IdentificadorJustificacionModalData = {
      configuracion: this.formPart.configuracion,
      othersPeriodosJustificacion: this.calendarioJustificacionDataSource.data.
        filter(element => element.value.id !== periodoJustificacion.value.id),
      periodoJustificacion,
    };

    const config = {
      data
    };

    const dialogRef = this.matDialog.open(IdentificadorJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>) => {
        if (modalData) {
          this.formPart.updatePeriodoJustificacion(modalData);
        }
      }
    );
  }
}
