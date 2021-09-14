import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PartidaPresupuestariaModalComponent, PartidaPresupuestariaModalComponentData } from '../../../shared/partida-presupuestaria-modal/partida-presupuestaria-modal.component';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaPartidaPresupuestariaFragment } from './convocatoria-partidas-presupuestarias.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_PARTIDA_PRESUPUESTARIA_KEY = marker('csp.convocatoria-partida-presupuestaria');

@Component({
  selector: 'sgi-convocatoria-partida-presupuestaria',
  templateUrl: './convocatoria-partidas-presupuestarias.component.html',
  styleUrls: ['./convocatoria-partidas-presupuestarias.component.scss']
})
export class ConvocatoriaPartidaPresupuestariaComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaPartidaPresupuestariaFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['codigo', 'tipoPartida', 'descripcion', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaPartidaPresupuestaria>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_PARTIDA() {
    return TIPO_PARTIDA_MAP;
  }

  constructor(
    public actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.PARTIDAS_PRESUPUESTARIAS, actionService);
    this.formPart = this.fragment as ConvocatoriaPartidaPresupuestariaFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaPartidaPresupuestaria>, property: string) => {
        switch (property) {
          case 'codigo':
            return wrapper.value.codigo;
          case 'descripcion':
            return wrapper.value.descripcion;
          case 'tipoPartida':
            return wrapper.value.tipoPartida;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.partidasPresupuestarias$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_PARTIDA_PRESUPUESTARIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_PARTIDA_PRESUPUESTARIA_KEY,
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
   * Apertura de modal de partida presupuestaria (edición/creación)
   *
   * @param partidaPresupuestariaActualizar partida presupuestaria que se carga en el modal para modificarlo.
   */
  openModalPartidaPresupuestaria(
    partidaPresupuestariaActualizar?: StatusWrapper<IConvocatoriaPartidaPresupuestaria>,
    canEdit?: boolean): void {
    const modalData: PartidaPresupuestariaModalComponentData = {
      partidaPresupuestaria: partidaPresupuestariaActualizar?.value ?? {} as IConvocatoriaPartidaPresupuestaria,
      partidasPresupuestarias: this.dataSource.data.map(element => element.value),
      convocatoriaPartidaPresupuestaria: null,
      readonly: this.formPart.readonly,
      canEdit: canEdit ?? true
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: modalData,
    };
    const dialogRef = this.matDialog.open(PartidaPresupuestariaModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (partidaPresupuestariaModal: IConvocatoriaPartidaPresupuestaria) => {
        if (!partidaPresupuestariaModal) {
          return;
        }
        if (!partidaPresupuestariaActualizar) {
          this.formPart.addPartidaPresupuestaria(partidaPresupuestariaModal);
        } else if (!partidaPresupuestariaActualizar.created) {
          partidaPresupuestariaActualizar.setEdited();
          this.formPart.setChanges(true);
        }
      }
    );
  }

  /**
   * Desactivar convocatoria partida
   */
  deletePartida(wrapper: StatusWrapper<IConvocatoriaPartidaPresupuestaria>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePartidaPresupuestaria(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
