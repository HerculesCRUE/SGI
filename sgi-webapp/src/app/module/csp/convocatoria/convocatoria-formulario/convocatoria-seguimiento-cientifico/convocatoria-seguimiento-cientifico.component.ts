import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaSeguimientoCientificoModalComponent, IConvocatoriaSeguimientoCientificoModalData } from '../../modals/convocatoria-seguimiento-cientifico-modal/convocatoria-seguimiento-cientifico-modal.component';
import { ConvocatoriaSeguimientoCientificoFragment } from './convocatoria-seguimiento-cientifico.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_KEY = marker('csp.convocatoria-periodo-seguimiento-cientifico');

@Component({
  selector: 'sgi-convocatoria-seguimiento-cientifico',
  templateUrl: './convocatoria-seguimiento-cientifico.component.html',
  styleUrls: ['./convocatoria-seguimiento-cientifico.component.scss']
})
export class ConvocatoriaSeguimientoCientificoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaSeguimientoCientificoFragment;
  private subscriptions: Subscription[] = [];

  columnas = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicio', 'fechaFin', 'tipoSeguimiento', 'observaciones', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get TIPO_SEGUIMIENTO_MAP() {
    return TIPO_SEGUIMIENTO_MAP;
  }

  constructor(
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.SEGUIMIENTO_CIENTIFICO, actionService);
    this.formPart = this.fragment as ConvocatoriaSeguimientoCientificoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>, property: string) => {
        switch (property) {
          case 'numPeriodo':
            return wrapper.value.numPeriodo;
          case 'mesInicial':
            return wrapper.value.mesInicial;
          case 'mesFinal':
            return wrapper.value.mesFinal;
          case 'fechaInicio':
            return wrapper.value.fechaInicioPresentacion;
          case 'fechaFin':
            return wrapper.value.fechaFinPresentacion;
          case 'tipoSeguimiento':
            return wrapper.value.tipoSeguimiento;
          default:
            return wrapper[property];
        }
      };
    this.sort.sort(({ id: 'numPeriodo', start: 'asc' }) as MatSortable);
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.seguimientosCientificos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_KEY,
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
   * Apertura de modal de seguimiento cientifico (edición/creación)
   *
   * @param seguimientoCientificoActualizar seguimiento cientifico que se carga en el modal para modificarlo.
   */
  openModalSeguimientoCientifico(seguimientoCientificoActualizar?: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>): void {
    const modalData: IConvocatoriaSeguimientoCientificoModalData = {
      duracion: this.actionService.duracion,
      convocatoriaSeguimientoCientifico: seguimientoCientificoActualizar
        ? seguimientoCientificoActualizar.value : {} as IConvocatoriaPeriodoSeguimientoCientifico,
      convocatoriaSeguimientoCientificoList: this.dataSource.data
    };

    const config = {
      data: modalData,
    };
    const dialogRef = this.matDialog.open(ConvocatoriaSeguimientoCientificoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (periodoJustificacionModal: IConvocatoriaPeriodoSeguimientoCientifico) => {
        if (!periodoJustificacionModal) {
          return;
        }

        if (!seguimientoCientificoActualizar) {
          this.formPart.addSeguimientoCientifico(periodoJustificacionModal);
        } else if (!seguimientoCientificoActualizar.created) {
          seguimientoCientificoActualizar.setEdited();
          this.formPart.setChanges(true);
        }
        this.recalcularNumPeriodos();
      }
    );
  }

  /**
   * Muestra la confirmacion para eliminar seguimiento cientifico
   *
   * @param seguimientoCientifico seguimiento cientifico que se quiere eliminar
   */
  deleteSeguimientoCientifico(seguimientoCientifico?: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteSeguimientoCientifico(seguimientoCientifico);
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

    this.formPart.seguimientosCientificos$.next(this.dataSource.data);
    this.formPart.checkFirstPeriodoStartsAtOne();
  }

}
