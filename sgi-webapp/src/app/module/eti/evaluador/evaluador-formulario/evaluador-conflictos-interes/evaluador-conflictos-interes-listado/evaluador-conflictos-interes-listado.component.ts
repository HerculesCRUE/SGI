import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { EvaluadorActionService } from '../../../evaluador.action.service';
import { EvaluadorConflictosInteresModalComponent } from '../evaluador-conflictos-interes-modal/evaluador-conflictos-interes-modal.component';
import { EvaluadorConflictosInteresFragment } from './evaluador-conflictos-interes-listado.fragment';

const MSG_CONFIRM_DELETE = marker('msg.delete.entity');
const CONFLICTO_INTERES_KEY = marker('eti.evaluador.conflicto-interes');

@Component({
  selector: 'sgi-evaluador-conflictos-interes',
  templateUrl: './evaluador-conflictos-interes-listado.component.html',
  styleUrls: ['./evaluador-conflictos-interes-listado.component.scss']
})
export class EvaluadorConflictosInteresListadoComponent extends FragmentComponent implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  private listadoFragment: EvaluadorConflictosInteresFragment;

  datasource: MatTableDataSource<StatusWrapper<IConflictoInteres>> = new MatTableDataSource<StatusWrapper<IConflictoInteres>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  textoDelete: string;
  msgParamConflictoInteresEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly dialogService: DialogService,
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService,
    protected matDialog: MatDialog,
    protected readonly snackBarService: SnackBarService,
    actionService: EvaluadorActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.CONFLICTO_INTERES, actionService);
    this.listadoFragment = this.fragment as EvaluadorConflictosInteresFragment;

    this.displayedColumns = ['persona', 'nombreCompleto', 'acciones'];

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.listadoFragment.conflictos$.subscribe((conflictoInteres) => {
      this.datasource.data = conflictoInteres;
    });
    this.datasource.paginator = this.paginator;
    this.datasource.sort = this.sort;

    this.datasource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConflictoInteres>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.personaConflicto);
          case 'nombreCompleto':
            return wrapper.value.personaConflicto.nombre
              + ' ' + wrapper.value.personaConflicto.apellidos;
          default:
            return wrapper.value[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      CONFLICTO_INTERES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRM_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      CONFLICTO_INTERES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConflictoInteresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  /**
   * Abre la ventana modal para añadir un nuevo conflicto de interés
   */
  openModalAddConflicto(): void {
    const conflictos: IConflictoInteres[] = this.listadoFragment.conflictos$.value.map(conflicto => conflicto.value);

    const config: MatDialogConfig = {
      panelClass: 'sgi-dialog-container',
      minWidth: '700px',
      data: conflictos
    };

    const dialogRef = this.matDialog.open(EvaluadorConflictosInteresModalComponent, config);

    dialogRef.afterClosed().subscribe(
      (conflictoAniadido: IConflictoInteres) => {
        if (conflictoAniadido) {
          this.listadoFragment.addConflicto(conflictoAniadido);
        }
      });
  }

  /** Elimina el conflicto de interés
   *
   * @param wrappedConflictoInteres el conflicto de interes a eliminar
   */
  delete(wrappedConflictoInteres: StatusWrapper<IConflictoInteres>): void {
    const dialogSubscription = this.dialogService.showConfirmation(this.textoDelete
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteConflictoInteres(wrappedConflictoInteres);
      }
    });
  }
}
