import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaPeriodosJustificacionModalComponent, IConvocatoriaPeriodoJustificacionModalData } from '../../modals/convocatoria-periodos-justificacion-modal/convocatoria-periodos-justificacion-modal.component';
import { ConvocatoriaPeriodosJustificacionFragment } from './convocatoria-periodos-justificacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_PERIODO_JUSTIFICACION_KEY = marker('csp.convocatoria-periodo-justificacion');

@Component({
  selector: 'sgi-convocatoria-periodos-justificacion',
  templateUrl: './convocatoria-periodos-justificacion.component.html',
  styleUrls: ['./convocatoria-periodos-justificacion.component.scss']
})
export class ConvocatoriaPeriodosJustificacionComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaPeriodosJustificacionFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicioPresentacion', 'fechaFinPresentacion', 'tipo', 'observaciones', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IConvocatoriaPeriodoJustificacion>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  get TIPO_JUSTIFICACION_MAP() {
    return TIPO_JUSTIFICACION_MAP;
  }


  constructor(
    private dialogService: DialogService,
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.PERIODO_JUSTIFICACION, actionService);
    this.formPart = this.fragment as ConvocatoriaPeriodosJustificacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaPeriodoJustificacion>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaPeriodoJustificacion>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };
    this.sort.sort(({ id: 'numPeriodo', start: 'asc' }) as MatSortable);
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.periodosJustificacion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }


  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_PERIODO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_PERIODO_JUSTIFICACION_KEY,
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

  /**
   * Apertura de modal de periodos justificacion (edición/creación)
   *
   * @param periodoJustificacionActualizar Periodo justificacion que se carga en el modal para modificarlo.
   */
  openModalPeriodoJustificacion(periodoJustificacionActualizar?: StatusWrapper<IConvocatoriaPeriodoJustificacion>): void {
    const data: IConvocatoriaPeriodoJustificacionModalData = {
      duracion: this.actionService.duracion,
      convocatoriaPeriodoJustificacion: periodoJustificacionActualizar
        ? periodoJustificacionActualizar.value : {} as IConvocatoriaPeriodoJustificacion,
      convocatoriaPeriodoJustificacionList: this.dataSource.data,
    };

    const config = {
      data,
    };

    const dialogRef = this.matDialog.open(ConvocatoriaPeriodosJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (periodoJustificacionModal: IConvocatoriaPeriodoJustificacion) => {
        if (!periodoJustificacionModal) {
          return;
        }

        if (!periodoJustificacionActualizar) {
          this.formPart.addPeriodoJustificacion(periodoJustificacionModal);
        } else if (!periodoJustificacionActualizar.created) {
          periodoJustificacionActualizar.setEdited();
          this.formPart.setChanges(true);
        }

        this.recalcularNumPeriodos();
      }
    );

  }

  /**
   * Muestra la confirmacion para eliminar un periodo justificacion
   *
   * @param periodoJustificacion Periodo justificacion que se quiere eliminar
   */
  deletePeriodoJustificacion(periodoJustificacion?: StatusWrapper<IConvocatoriaPeriodoJustificacion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoJustificacion(periodoJustificacion);
            this.recalcularNumPeriodos();
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Recalcula los numeros de los periodos de todos los periodos de justificacion de la tabla en funcion de su mes inicial.
   */
  private recalcularNumPeriodos(): void {
    let numPeriodo = 1;
    this.dataSource.data
      .sort((a, b) => (a.value.mesInicial > b.value.mesInicial) ? 1 : ((b.value.mesInicial > a.value.mesInicial) ? -1 : 0));

    this.dataSource.data.forEach(c => {
      c.value.numPeriodo = numPeriodo++;
    });

    this.formPart.periodosJustificacion$.next(this.dataSource.data);
    this.formPart.checkFirstPeriodoStartsAtOne();
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
