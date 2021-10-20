import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { TIPO_ENTIDAD_MAP } from '@core/models/rel/relacion';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IProyectoRelacionModalData, ProyectoRelacionModalComponent } from '../../modals/proyecto-relacion-modal/proyecto-relacion-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoRelacionFragment, IProyectoRelacionTableData, TIPO_RELACION_MAP } from './proyecto-relaciones.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_RELACION_KEY = marker('csp.proyecto-relacion');

@Component({
  selector: 'sgi-proyecto-relaciones',
  templateUrl: './proyecto-relaciones.component.html',
  styleUrls: ['./proyecto-relaciones.component.scss']
})
export class ProyectoRelacionesComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoRelacionFragment;

  displayedColumns = ['tipoEntidadRelacionada', 'entidadRelacionada', 'tipoRelacion', 'observaciones', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoRelacionTableData>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_ENTIDAD_MAP() {
    return TIPO_ENTIDAD_MAP;
  }

  get TIPO_RELACION_MAP() {
    return TIPO_RELACION_MAP;
  }

  constructor(
    protected actionService: ProyectoActionService,
    private readonly translate: TranslateService,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.RELACIONES, actionService);
    this.formPart = this.fragment as ProyectoRelacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initProyectoRelacionesTable();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initProyectoRelacionesTable(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoRelacionTableData>, property: string) => {
        switch (property) {
          case 'tipoEntidadRelacionada':
            return wrapper.value.tipoEntidadRelacionada;
          case 'entidadRelacionada':
            return wrapper.value.entidadRelacionada.titulo;
          case 'tipoRelacion':
            return wrapper.value.tipoRelacion;
          case 'observaciones':
            return wrapper.value.observaciones;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.getInformesPatentabilidadTableData();
  }

  private getInformesPatentabilidadTableData(): void {
    this.subscriptions.push(this.formPart.getRelacionesProyectoTableData$()
      .subscribe(elements => this.dataSource.data = elements));
  }

  deleteRelacion(relacion: StatusWrapper<IProyectoRelacionTableData>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteRelacion(relacion);
          }
        }
      )
    );
  }

  openModal(wrapper?: StatusWrapper<IProyectoRelacionTableData>, rowIndex?: number): void {
    const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);
    const data: IProyectoRelacionModalData = {
      relacion: wrapper ? wrapper.value : {} as IProyectoRelacionTableData,
      readonly: !this.formPart.hasEditPerm(),
      entitiesAlreadyRelated: wrapper ? [] : [...this.getAlreadyRelatedEntities(wrapper), this.formPart.getCurrentProyectoAsSelfRelated()]
    };

    const config: MatDialogConfig = {
      panelClass: 'sgi-dialog-container',
      data,
      minWidth: '500px',
    };

    const dialogRef = this.matDialog.open(ProyectoRelacionModalComponent, config);
    dialogRef.afterClosed().subscribe((relacion: IProyectoRelacionTableData) => {
      if (relacion) {
        if (wrapper) {
          this.formPart.updateRelacion(row);
        } else {
          this.formPart.addRelacion(relacion);
        }
      }
    });
  }

  private getAlreadyRelatedEntities(wrapper?: StatusWrapper<IProyectoRelacionTableData>): IProyectoRelacionTableData[] {
    return this.dataSource.data
      .filter(element =>
        element.value.tipoEntidadRelacionada !== wrapper?.value.tipoEntidadRelacionada
        || element.value.entidadRelacionada.id !== wrapper?.value.entidadRelacionada?.id)
      .map(element => element.value)
  }

  isRelatedEntityReady(wrapper: StatusWrapper<IProyectoRelacionTableData>): boolean {
    return typeof wrapper.value.entidadRelacionada.titulo !== 'undefined';
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number): number {
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    return (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_RELACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_RELACION_KEY,
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
}
