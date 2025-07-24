import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAsistente } from '@core/models/eti/asistente';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { LanguageService } from '@core/services/language.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { takeUntil } from 'rxjs/operators';
import { Rol } from '../../../acta-rol';
import { ActaActionService } from '../../../acta.action.service';
import { ActaAsistentesEditarModalComponent } from '../acta-asistentes-editar-modal/acta-asistentes-editar-modal.component';
import { ActaAsistentesFragment } from './acta-asistentes-listado.fragment';

@Component({
  selector: 'sgi-acta-asistentes',
  templateUrl: './acta-asistentes-listado.component.html',
  styleUrls: ['./acta-asistentes-listado.component.scss']
})
export class ActaAsistentesListadoComponent extends FragmentComponent implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  datasource: MatTableDataSource<StatusWrapper<IAsistente>> = new MatTableDataSource<StatusWrapper<IAsistente>>();

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  readonly: boolean;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService,
    protected matDialog: MatDialog,
    private actionService: ActaActionService,
    private languageService: LanguageService
  ) {
    super(actionService.FRAGMENT.ASISTENTES, actionService);

    this.displayedColumns = ['evaluador.persona', 'evaluador.nombre', 'asistencia', 'motivo', 'acciones'];
  }

  ngOnInit() {
    super.ngOnInit();
    this.readonly = this.actionService.getRol() === Rol.EVALUADOR ? true : this.actionService.readonly;

    this.datasource.paginator = this.paginator;
    this.datasource.sort = this.sort;
    (this.fragment as ActaAsistentesFragment).asistentes$.pipe(takeUntil(this._destroy$)).subscribe((asistentes) => {
      this.datasource.data = asistentes;
    });
    this.datasource.sortingDataAccessor =
      (wrapper: StatusWrapper<IAsistente>, property: string) => {
        switch (property) {
          case 'motivo':
            return this.languageService.getFieldValue(wrapper.value.motivo);
          default:
            return wrapper.value[property];
        }
      };
  }

  /**
   * Abre la ventana modal para modificar una asistencia
   *
   * @param asistente asistente a modificar
   */
  openUpdateModal(asistente: StatusWrapper<IAsistente>): void {
    const config = {
      data: asistente.value
    };
    const dialogRef = this.matDialog.open(ActaAsistentesEditarModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (resultado: IAsistente) => {
        if (resultado) {
          asistente.setEdited();
          this.fragment.setChanges(true);
          this.fragment.setComplete(true);
        }
      }
    );
  }

  protected setupI18N(): void { }
}
