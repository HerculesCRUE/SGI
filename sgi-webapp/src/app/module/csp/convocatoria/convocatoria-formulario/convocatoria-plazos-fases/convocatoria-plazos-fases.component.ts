import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaPlazosFaseModalComponent, ConvocatoriaPlazosFaseModalComponentData } from '../../modals/convocatoria-plazos-fase-modal/convocatoria-plazos-fase-modal.component';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-plazos-fases.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR = marker('error.csp.convocatoria-fase.delete');
const CONVOCATORIA_FASE_KEY = marker('csp.convocatoria-fase');

@Component({
  selector: 'sgi-convocatoria-plazos-fases',
  templateUrl: './convocatoria-plazos-fases.component.html',
  styleUrls: ['./convocatoria-plazos-fases.component.scss']
})

export class ConvocatoriaPlazosFasesComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaPlazosFasesFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'observaciones', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  dataSource: MatTableDataSource<StatusWrapper<IConvocatoriaFase>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected snackBarService: SnackBarService,
    public actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.FASES, actionService);
    this.formPart = this.fragment as ConvocatoriaPlazosFasesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaFase>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.plazosFase$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_FASE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_FASE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  /**
   * Apertura de modal de plazos fase
   * @param plazo Identificador de plazos fase al guardar/editar
   */
  openModalPlazos(plazo?: StatusWrapper<IConvocatoriaFase>): void {
    const datosPlazosFases: ConvocatoriaPlazosFaseModalComponentData = {
      plazos: this.dataSource.data.filter(existing => existing !== plazo).map(wrapper => wrapper.value),
      plazo: plazo ? plazo.value : {} as IConvocatoriaFase,
      idModeloEjecucion: this.actionService.modeloEjecucionId,
      readonly: this.formPart.readonly,
      canEdit: this.formPart.canEdit
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: datosPlazosFases,
    };

    const dialogRef = this.matDialog.open(ConvocatoriaPlazosFaseModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ConvocatoriaPlazosFaseModalComponentData) => {
        if (modalData) {
          if (plazo) {
            if (!plazo.created) {
              plazo.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPlazosFases(modalData.plazo);
          }
        }
      }
    );
  }

  /**
   * Desactivar convocatoria fase
   */
  deleteFase(wrapper: StatusWrapper<IConvocatoriaFase>) {
    if (this.actionService.isDelete(wrapper.value)) {
      this.subscriptions.push(
        this.dialogService.showConfirmation(this.textoDelete).subscribe(
          (aceptado) => {
            if (aceptado) {
              this.formPart.deleteFase(wrapper);
            }
          }
        )
      );
    } else {
      this.snackBarService.showError(MSG_ERROR);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
