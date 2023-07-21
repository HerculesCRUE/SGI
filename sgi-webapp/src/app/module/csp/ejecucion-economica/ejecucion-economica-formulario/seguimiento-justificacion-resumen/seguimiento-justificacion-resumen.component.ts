import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { ISeguimientoJustificacionAnualidad } from '@core/models/csp/seguimiento-justificacion-anualidad';
import { ConfigService } from '@core/services/cnf/config.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IdentificadorJustificacionModalComponent, IdentificadorJustificacionModalData } from '../../modals/identificador-justificacion-modal/identificador-justificacion-modal.component';
import { PresentacionDocumentacionModalComponent } from '../../modals/presentacion-documentacion-modal/presentacion-documentacion-modal.component';
import { ISeguimientoGastosJustificadosResumenExportModalData, SeguimientoGastosJustificadosResumenExportModalComponent } from '../../modals/seguimiento-gastos-justificados-resumen-export-modal/seguimiento-gastos-justificados-resumen-export-modal.component';
import { ISeguimientoJustificacionAnualidadModalData, SeguimientoJustificacionAnualidadModalComponent } from '../../modals/seguimiento-justificacion-anualidad-modal/seguimiento-justificacion-anualidad-modal.component';
import { ISeguimientoJustificacionModalData, SeguimientoJustificacionModalComponent } from '../../modals/seguimiento-justificacion-modal/seguimiento-justificacion-modal.component';
import {
  IProyectoPeriodoJustificacionWithTituloProyecto, IProyectoPeriodoSeguimientoWithTituloProyecto,
  IProyectoSeguimientoEjecucionEconomicaData, IProyectoSeguimientoJustificacionWithFechaJustificacion,
  SeguimientoJustificacionResumenFragment
} from './seguimiento-justificacion-resumen.fragment';

@Component({
  selector: 'sgi-seguimiento-justificacion-resumen',
  templateUrl: './seguimiento-justificacion-resumen.component.html',
  styleUrls: ['./seguimiento-justificacion-resumen.component.scss']
})
export class SeguimientoJustificacionResumenComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SeguimientoJustificacionResumenFragment;

  private totalElementos = 0;
  private limiteRegistrosExportacionExcel: string;

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

  seguimientosJustificacionElementosPagina = [5, 10, 25, 100];
  seguimientosJustificacionDisplayedColumns = [
    'proyectoId',
    'fechaUltimaJustificacion',
    'importeJustificado',
    'importeAceptado',
    'importeRechazado',
    'importeAlegado',
    'importeNoEjecutado',
    'importeReintegrar',
    'importeReintegrado',
    'interesesReintegrar',
    'interesesReintegrados',
    'acciones',
  ];

  seguimientosJustificacionAnualidadElementosPagina = [5, 10, 25, 100];
  seguimientosJustificacionAnualidadDisplayedColumns = [
    'proyectoId',
    'identificadorJustificacion',
    'fechaPresentacionJustificacion',
    'anio',
    'importeJustificado',
    'importeAceptado',
    'importeRechazado',
    'importeAlegado',
    'importeNoEjecutado',
    'importeReintegrar',
    'importeReintegrado',
    'interesesReintegrar',
    'interesesReintegrados',
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

  calendarioSeguimientoElementosPagina = [5, 10, 25, 100];
  calendarioSeguimientoDisplayedColumns = [
    'proyectoId',
    'tituloProyecto',
    'numPeriodo',
    'fechaInicio',
    'fechaFin',
    'fechaInicioPresentacion',
    'fechaFinPresentacion',
    'tipoSeguimiento',
    'fechaPresentacionDocumentacion',
    'acciones',
  ];

  proyectosSGIDataSource = new MatTableDataSource<IProyectoSeguimientoEjecucionEconomicaData>();
  seguimientosJustificacionDataSource = new
    MatTableDataSource<StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>>();
  seguimientosJustificacionAnualidadDataSource = new
    MatTableDataSource<StatusWrapper<ISeguimientoJustificacionAnualidad>>();
  calendarioJustificacionDataSource = new MatTableDataSource<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>>();
  calendarioSeguimientoDataSource = new MatTableDataSource<StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>>();
  @ViewChild('proyectosSGIPaginator', { static: true }) proyectosSGIPaginator: MatPaginator;
  @ViewChild('proyectosSGISort', { static: true }) proyectosSGISort: MatSort;
  @ViewChild('seguimientosJustificacionPaginator', { static: true })
  seguimientosJustificacionPaginator: MatPaginator;
  @ViewChild('seguimientosJustificacionAnualidadSort', { static: true }) seguimientosJustificacionAnualidadSort: MatSort;
  @ViewChild('seguimientosJustificacionAnualidadPaginator', { static: true })
  seguimientosJustificacionAnualidadPaginator: MatPaginator;
  @ViewChild('seguimientosJustificacionSort', { static: true }) seguimientosJustificacionSort: MatSort;
  @ViewChild('calendarioJustificacionPaginator', { static: true }) calendarioJustificacionPaginator: MatPaginator;
  @ViewChild('calendarioJustificacionSort', { static: true }) calendarioJustificacionSort: MatSort;
  @ViewChild('calendarioSeguimientoPaginator', { static: true }) calendarioSeguimientoPaginator: MatPaginator;
  @ViewChild('calendarioSeguimientoSort', { static: true }) calendarioSeguimientoSort: MatSort;

  get CSP_ROUTE_NAMES() {
    return CSP_ROUTE_NAMES;
  }

  get TIPO_JUSTIFICACION_MAP() {
    return TIPO_JUSTIFICACION_MAP;
  }

  get TIPO_SEGUIMIENTO_MAP() {
    return TIPO_SEGUIMIENTO_MAP;
  }

  constructor(
    private readonly actionService: EjecucionEconomicaActionService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService) {
    super(actionService.FRAGMENT.SEGUIMIENTO_JUSTIFICACION_RESUMEN, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionResumenFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initProyectosSGITable();
    this.initSeguimientoJustificacionTable();
    this.initSeguimientoJustificacionAnualidadTable();
    this.initCalendarioJustificacionTable();
    this.initCalendarioSeguimientoTable();

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-seguimiento-justificacion-resumen').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private initProyectosSGITable(): void {
    this.proyectosSGIDataSource.paginator = this.proyectosSGIPaginator;
    this.proyectosSGIDataSource.sort = this.proyectosSGISort;
    this.subscriptions.push(this.formPart.getProyectosSGI$().subscribe(elements => {
      this.proyectosSGIDataSource.data = elements;
    }));
  }

  private initSeguimientoJustificacionTable(): void {
    this.seguimientosJustificacionDataSource.paginator = this.seguimientosJustificacionPaginator;
    this.seguimientosJustificacionDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>, property: string) => {
        switch (property) {
          case 'proyectoId':
            return wrapper.value?.proyectoProyectoSge?.proyecto?.id;
          default:
            return wrapper.value[property];
        }
      };
    this.seguimientosJustificacionDataSource.sort = this.seguimientosJustificacionSort;
    this.subscriptions.push(this.formPart.getSeguimientosJustificacion$().subscribe(elements => {
      this.seguimientosJustificacionDataSource.data = elements;
      this.totalElementos = elements.length;
    }));
  }

  private initSeguimientoJustificacionAnualidadTable(): void {
    this.seguimientosJustificacionAnualidadDataSource.paginator = this.seguimientosJustificacionAnualidadPaginator;
    this.seguimientosJustificacionAnualidadDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ISeguimientoJustificacionAnualidad>, property: string) => {
        switch (property) {
          case 'anio':
            return wrapper.value.proyectoPeriodoJustificacionSeguimiento?.proyectoAnualidad?.anio ?? wrapper.value[property];
          default:
            return wrapper.value[property] ??
              (wrapper.value.proyectoPeriodoJustificacionSeguimiento && wrapper.value.proyectoPeriodoJustificacionSeguimiento[property]);
        }
      };
    this.seguimientosJustificacionAnualidadDataSource.sort = this.seguimientosJustificacionAnualidadSort;
    this.subscriptions.push(this.formPart.getSeguimientosJustificacionAnualidad$().subscribe(elements => {
      this.seguimientosJustificacionAnualidadDataSource.data = elements;
    }));
  }

  private initCalendarioJustificacionTable(): void {
    this.calendarioJustificacionDataSource.paginator = this.calendarioJustificacionPaginator;
    this.calendarioJustificacionDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>, property: string) => {
        switch (property) {
          case 'proyecto.id':
            return wrapper.value?.proyecto?.id;
          case 'tipoJustificacion':
            return TIPO_JUSTIFICACION_MAP.get(wrapper.value.tipoJustificacion);
          default:
            return wrapper.value[property];
        }
      };
    this.calendarioJustificacionDataSource.sort = this.calendarioJustificacionSort;
    this.subscriptions.push(this.formPart.getPeriodosJustificacion$().subscribe(elements => {
      this.calendarioJustificacionDataSource.data = elements;
    }));
  }

  private initCalendarioSeguimientoTable(): void {
    this.calendarioSeguimientoDataSource.paginator = this.calendarioSeguimientoPaginator;
    this.calendarioSeguimientoDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>, property: string) => {
        switch (property) {
          case 'tipoSeguimiento':
            return TIPO_SEGUIMIENTO_MAP.get(wrapper.value?.tipoSeguimiento);
          default:
            return wrapper.value[property];
        }
      };
    this.calendarioSeguimientoDataSource.sort = this.calendarioSeguimientoSort;
    this.subscriptions.push(this.formPart.getPeriodosSeguimiento$().subscribe(elements => {
      this.calendarioSeguimientoDataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getSeguimientoJustificacionAnualidadAnio(seguimientoJustificacionAnualidad: StatusWrapper<ISeguimientoJustificacionAnualidad>): number {
    return seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.proyectoAnualidad?.anio
      ?? seguimientoJustificacionAnualidad.value.anio;
  }

  openModalSeguimientoJustificacion(
    seguimientoJustificacion: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>): void {
    const data: ISeguimientoJustificacionModalData = {
      proyecto: this.proyectosSGIDataSource.data.find(
        proyectoSGI => proyectoSGI.id === seguimientoJustificacion.value.proyectoProyectoSge.id
      ),
      seguimientoJustificacion
    };

    const config: MatDialogConfig<ISeguimientoJustificacionModalData> = {
      data
    };

    const dialogRef = this.matDialog.open(SeguimientoJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>) => {
        if (modalData) {
          if (modalData.value.id) {
            this.formPart.updateSeguimientoJustificacion(modalData);
          } else {
            this.formPart.createSeguimientoJustificacion(modalData);
          }
        }
      }
    );
  }

  openModalSeguimientoJustificacionAnualidad(
    seguimientoJustificacionAnualidad: StatusWrapper<ISeguimientoJustificacionAnualidad>): void {
    const data: ISeguimientoJustificacionAnualidadModalData = {
      tituloProyecto: this.proyectosSGIDataSource.data.find(
        proyectoSGI => proyectoSGI.id === seguimientoJustificacionAnualidad.value.proyectoId
      )?.nombre,
      seguimientoJustificacionAnualidad
    };

    const config: MatDialogConfig<ISeguimientoJustificacionAnualidadModalData> = {
      data
    };

    const dialogRef = this.matDialog.open(SeguimientoJustificacionAnualidadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: StatusWrapper<ISeguimientoJustificacionAnualidad>) => {
        if (modalData) {
          if (modalData.value?.proyectoPeriodoJustificacionSeguimiento?.id) {
            this.formPart.updateSeguimientoJustificacionAnualidad(modalData);
          } else {
            this.formPart.createSeguimientoJustificacionAnualidad(modalData);
          }
        }
      }
    );
  }

  openModalPeriodoJustificacion(periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>): void {
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
          const { tituloProyecto, ...proyectoPeriodoJustificacion } = modalData.value;
          this.formPart.updatePeriodoJustificacion(modalData);
        }
      }
    );
  }

  openModalPeriodoSeguimiento(periodoSeguimiento: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>): void {
    const config: MatDialogConfig<StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>> = {
      data: periodoSeguimiento
    };

    const dialogRef = this.matDialog.open(PresentacionDocumentacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>) => {
        if (modalData) {
          this.formPart.updatePeriodoSeguimiento(modalData);
        }
      }
    );
  }

  public openExportModal(): void {
    const data: ISeguimientoGastosJustificadosResumenExportModalData = {
      findOptions: {},
      proyectoSgeRef: this.formPart.proyectoSgeRef,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(SeguimientoGastosJustificadosResumenExportModalComponent, config);
  }
}
