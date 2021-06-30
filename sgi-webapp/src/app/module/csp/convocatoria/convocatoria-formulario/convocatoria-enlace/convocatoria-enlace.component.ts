import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaEnlaceModalComponent, ConvocatoriaEnlaceModalComponentData } from '../../modals/convocatoria-enlace-modal/convocatoria-enlace-modal.component';
import { ConvocatoriaEnlaceFragment } from './convocatoria-enlace.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_ENLACE_KEY = marker('csp.convocatoria-enlace');

@Component({
  selector: 'sgi-convocatoria-enlace',
  templateUrl: './convocatoria-enlace.component.html',
  styleUrls: ['./convocatoria-enlace.component.scss']
})
export class ConvocatoriaEnlaceComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaEnlaceFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['url', 'descripcion', 'tipoEnlace', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaEnlace>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.ENLACES, actionService);
    this.formPart = this.fragment as ConvocatoriaEnlaceFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaEnlace>, property: string) => {
        switch (property) {
          case 'url':
            return wrapper.value.url;
          case 'descripcion':
            return wrapper.value.descripcion;
          case 'tipoEnlace':
            return wrapper.value.tipoEnlace.nombre;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.enlace$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ENLACE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_ENLACE_KEY,
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
   * Abre modal con el modelo convocatoria enlace seleccionada
   * @param wrapper convocatoria enlace
   */
  openModal(wrapper?: StatusWrapper<IConvocatoriaEnlace>): void {
    const data: ConvocatoriaEnlaceModalComponentData = {
      enlace: wrapper?.value ?? {} as IConvocatoriaEnlace,
      idModeloEjecucion: this.actionService.modeloEjecucionId,
      selectedUrls: this.formPart.getSelectedUrls(),
      readonly: this.formPart.readonly,
      canEdit: this.formPart.canEdit,
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ConvocatoriaEnlaceModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ConvocatoriaEnlaceModalComponentData) => {
        if (result) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addEnlace(result.enlace);
          }
        }
      }
    );
  }

  /**
   * Desactivar convocatoria enlace
   */
  deleteEnlace(wrapper: StatusWrapper<IConvocatoriaEnlace>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteEnlace(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
