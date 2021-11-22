import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IActa } from '@core/models/eti/acta';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaEvaluacionesFragment } from './memoria-evaluaciones.fragment';

interface IActaConvocatoriaReunion {
  idConvocatoriaReunion: number;
  acta: IActa;
}

@Component({
  selector: 'sgi-memoria-evaluaciones',
  templateUrl: './memoria-evaluaciones.component.html',
  styleUrls: ['./memoria-evaluaciones.component.scss']
})
export class MemoriaEvaluacionesComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: MemoriaEvaluacionesFragment;
  private subscriptions: Subscription[] = [];
  private actasConvocatoriaReunion: IActaConvocatoriaReunion[] = [];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  displayedColumns: string[] = ['tipoEvaluacion', 'version', 'dictamen', 'informeEvaluacion',
    'informeFavorable'];
  elementosPagina: number[] = [5, 10, 25, 100];
  dataSource: MatTableDataSource<StatusWrapper<IEvaluacion>> = new MatTableDataSource<StatusWrapper<IEvaluacion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly dialogService: DialogService,
    protected matDialog: MatDialog,
    protected memoriaService: MemoriaService,
    protected evaluacionService: EvaluacionService,
    protected documentoService: DocumentoService,
    actionService: MemoriaActionService,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService) {

    super(actionService.FRAGMENT.EVALUACIONES, actionService);
    this.formPart = this.fragment as MemoriaEvaluacionesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource = new MatTableDataSource<StatusWrapper<IEvaluacion>>();
    this.subscriptions.push(this.formPart.evaluaciones$.subscribe(elements => {
      this.dataSource.data = elements;

      elements.forEach(evaluacionWrapper => {
        this.subscriptions.push(this.convocatoriaReunionService.findActaInConvocatoriaReunion(evaluacionWrapper.value.convocatoriaReunion.id).subscribe(acta => {
          this.actasConvocatoriaReunion.push({ acta, idConvocatoriaReunion: evaluacionWrapper.value.convocatoriaReunion.id } as IActaConvocatoriaReunion);
        }));
      });

    }));

    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEvaluacion>, property: string) => {
        switch (property) {
          case 'tipoEvaluacion':
            return wrapper.value.tipoEvaluacion?.nombre;
          case 'dictamen':
            return wrapper.value.dictamen?.nombre;
          default:
            return wrapper.value[property];
        }
      };

  }

  hasInformeEvaluacion(evaluacion: StatusWrapper<IEvaluacion>): boolean {
    return ((evaluacion.value.tipoEvaluacion.id === 2 && (evaluacion.value.dictamen?.id === 2
      || evaluacion.value.dictamen?.id === 3 || evaluacion.value.dictamen?.id === 4))
      || (evaluacion.value.tipoEvaluacion.id === 4 && evaluacion.value.dictamen?.id === 8)
    );
  }

  hasInformeFavorable(evaluacion: StatusWrapper<IEvaluacion>): boolean {
    return ((evaluacion.value.tipoEvaluacion.id === 1 && evaluacion.value.dictamen?.id === 9)
      || (evaluacion.value.tipoEvaluacion.id === 2 && evaluacion.value.dictamen?.id === 1)
    );
  }

  isActaFinalizada(evaluacionWrapper: StatusWrapper<IEvaluacion>): boolean {
    const actaConvocatoriaReunion = this.actasConvocatoriaReunion.find(actaConv => actaConv.idConvocatoriaReunion === evaluacionWrapper.value.convocatoriaReunion.id);
    return actaConvocatoriaReunion?.acta?.estadoActual?.id === 2 ? true : false;
  }

  /**
   * Visualiza el informe de evaluación seleccionado.
   * @param idEvaluacion id de la evaluación del informe
   */
  visualizarInforme(idEvaluacion: number): void {
    const documento: IDocumento = {} as IDocumento;
    this.evaluacionService.getDocumentoEvaluacion(idEvaluacion).pipe(
      switchMap((documentoInfo: IDocumento) => {
        documento.nombre = documentoInfo.nombre;
        return this.documentoService.downloadFichero(documentoInfo.documentoRef);
      })
    ).subscribe(response => {
      triggerDownloadToUser(response, documento.nombre);
    });
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

}
