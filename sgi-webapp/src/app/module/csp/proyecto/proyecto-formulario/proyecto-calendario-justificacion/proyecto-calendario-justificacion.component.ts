import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_PERIODO_SEGUIMIENTO_ID_KEY } from '../../../proyecto-periodo-seguimiento/proyecto-periodo-seguimiento.action.service';
import { IProyectoPeriodoJustificacionModalData, ProyectoPeriodoJustificacionModalComponent } from '../../modals/proyecto-periodo-justificacion-modal/proyecto-periodo-justificacion-modal/proyecto-periodo-justificacion-modal.component';
import { PROYECTO_ROUTE_NAMES } from '../../proyecto-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { IPeriodoJustificacionListado, ProyectoCalendarioJustificacionFragment } from './proyecto-calendario-justificacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_PERIODO_JUSTIFICACION_KEY = marker('csp.proyecto-periodo-justificacion');

@Component({
  selector: 'sgi-proyecto-calendario-justificacion',
  templateUrl: './proyecto-calendario-justificacion.component.html',
  styleUrls: ['./proyecto-calendario-justificacion.component.scss']
})
export class ProyectoCalendarioJustificacionComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  formPart: ProyectoCalendarioJustificacionFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina: number[] = [5, 10, 25, 100];
  displayedColumns = ['helpIcon', 'numPeriodo', 'fechaInicio', 'fechaFin', 'fechaInicioPresentacion', 'fechaFinPresentacion', 'tipoJustificacion', 'observaciones', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<IPeriodoJustificacionListado>();
  @ViewChild('paginator', { static: true }) paginator: MatPaginator;
  @ViewChild('sort', { static: true }) sort: MatSort;

  get TIPO_JUSTIFICACION_MAP() {
    return TIPO_JUSTIFICACION_MAP;
  }

  get PROYECTO_ROUTE_NAMES() {
    return PROYECTO_ROUTE_NAMES;
  }

  constructor(
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.CALENDARIO_JUSTIFICACION, actionService);
    this.formPart = this.fragment as ProyectoCalendarioJustificacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource = new MatTableDataSource<IPeriodoJustificacionListado>();
    this.dataSource.paginator = this.paginator;
    this.subscriptions.push(this.formPart.periodoJustificaciones$.subscribe(
      (proyectoPeridosJustificaciones) => {
        this.dataSource.data = proyectoPeridosJustificaciones;
      }));
    this.dataSource.sortingDataAccessor =
      (periodoJustificacionListado: IPeriodoJustificacionListado, property: string) => {
        switch (property) {
          default:
            return periodoJustificacionListado[property];
        }
      };
    this.dataSource.sort = this.sort;
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_KEY,
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

  deletePeriodoJustificacion(index: number, wrapper: StatusWrapper<IProyectoPeriodoJustificacion>) {
    const messageConfirmation = this.textoDelete;
    this.subscriptions.push(
      this.dialogService.showConfirmation(messageConfirmation).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deletePeriodoJustificacion(index, wrapper);
          }
        }
      )
    );
    this.recalcularNumPeriodos();
  }

  getConvocatoriaPeriodoSeguimientoState(convocatoriaPeriodoSeguimientoId: number) {
    return { [CONVOCATORIA_PERIODO_SEGUIMIENTO_ID_KEY]: convocatoriaPeriodoSeguimientoId };
  }

  /**
   * Apertura de modal de Periodos de justificacion (edición/creación)
   * @param idHito Identificador de hito a editar.
   */
  openModal(periodoJustificacionActualizar?: IPeriodoJustificacionListado, rowIndex?: number): void {

    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const proyectoPeriodoJusficacionTabla = this.dataSource.data
      .filter(periodoJusficacion => periodoJusficacion.proyectoPeriodoJustificacion)
      .map(periodoJusficacion => periodoJusficacion.proyectoPeriodoJustificacion.value);

    proyectoPeriodoJusficacionTabla.splice(row, 1);

    let proyectoPeriodoJustificacion: IProyectoPeriodoJustificacion;
    if (periodoJustificacionActualizar?.proyectoPeriodoJustificacion) {
      proyectoPeriodoJustificacion = periodoJustificacionActualizar?.proyectoPeriodoJustificacion?.value;
      proyectoPeriodoJustificacion.numPeriodo = periodoJustificacionActualizar?.numPeriodo;
    }

    const data: IProyectoPeriodoJustificacionModalData = {
      proyectoPeriodoJustificacion,
      proyectoPeriodoJustificacionList: this.dataSource.data.filter(
        periodoProyectoJustificacion => periodoProyectoJustificacion?.proyectoPeriodoJustificacion).map(
          periodoJustificacion => periodoJustificacion?.proyectoPeriodoJustificacion?.value),
      convocatoriaPeriodoJustificacion: periodoJustificacionActualizar?.convocatoriaPeriodoJustificacion,
      proyecto: this.formPart.proyecto,
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };

    const dialogRef = this.matDialog.open(ProyectoPeriodoJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: IProyectoPeriodoJustificacionModalData) => {
        if (modalData.proyectoPeriodoJustificacion) {
          if (!periodoJustificacionActualizar) {
            const periodoJustificacion = modalData.proyectoPeriodoJustificacion;
            this.formPart.addPeriodoJustificacion(periodoJustificacion, modalData.proyectoPeriodoJustificacion?.id);
          } else {
            const periodoJustificacion = new StatusWrapper<IProyectoPeriodoJustificacion>(modalData.proyectoPeriodoJustificacion);
            this.formPart.updatePeriodoJustificacion(periodoJustificacion, row);
          }
        }
        this.recalcularNumPeriodos();
      }
    );
  }


  /**
   * Recalcula los numeros de los periodos de todos los periodos de justificacion de la tabla en funcion de su mes inicial.
   */
  private recalcularNumPeriodos(): void {
    let numPeriodo = 1;
    this.dataSource.data
      .sort((a, b) => (a.fechaInicio > b.fechaFin) ? 1 : ((b.fechaInicio > a.fechaFin) ? -1 : 0));

    this.dataSource.data.forEach(c => {
      c.numPeriodo = numPeriodo++;
    });

    this.formPart.periodoJustificaciones$.next(this.dataSource.data);
  }

}
