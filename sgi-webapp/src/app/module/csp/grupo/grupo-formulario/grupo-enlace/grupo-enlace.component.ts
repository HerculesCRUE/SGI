import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoEnlaceModalComponent, GrupoEnlaceModalData } from '../../modals/grupo-enlace-modal/grupo-enlace-modal.component';
import { GrupoEnlaceFragment } from './grupo-enlace.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_ENLACE_KEY = marker('csp.grupo-enlace');
const MODAL_TITLE_KEY = marker('csp.grupo-enlace');

@Component({
  selector: 'sgi-grupo-enlace',
  templateUrl: './grupo-enlace.component.html',
  styleUrls: ['./grupo-enlace.component.scss']
})
export class GrupoEnlaceComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoEnlaceFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['enlace', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoEnlace>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }


  constructor(
    protected proyectoService: GrupoService,
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.ENLACE, actionService);
    this.formPart = this.fragment as GrupoEnlaceFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoEnlace>, property: string) => {
        switch (property) {
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.enlaces$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {

    this.translate.get(
      GRUPO_ENLACE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_ENLACE_KEY,
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
   * Apertura de modal de enlaces (edición/creación)
   *
   * @param wrapper StatusWrapper<IGrupoEnlace>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IGrupoEnlace>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: GrupoEnlaceModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IGrupoEnlace,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(GrupoEnlaceModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoEnlaceModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupo = this.actionService.grupo;
            this.formPart.addGrupoEnlace(modalData.entidad as IGrupoEnlace);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoEnlace>(modalData.entidad as IGrupoEnlace);
            this.formPart.updateGrupoEnlace(entidad);
          }
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar grupo equipo
   */
  deleteEquipo(wrapper: StatusWrapper<IGrupoEnlace>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoEnlace(wrapper);
          }
        }
      )
    );
  }

}
