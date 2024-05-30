import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { DialogService } from '@core/services/dialog.service';
import { Status, StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription, forkJoin, of } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionInventorModalComponent, InvencionInventorModalData } from '../../modals/invencion-inventor-modal/invencion-inventor-modal.component';
import { InvencionInventorFragment } from './invencion-inventor.fragment';
import { InvencionInventorService } from '@core/services/pii/invencion-inventor/invencion-inventor.service';

const INVENCION_INVENTOR_KEY = marker('pii.invencion.inventor');
const INVENCION_INVENTOR_PARTICIPACION_ERROR = marker('pii.invencion-inventor.error.participacion');
const INVENCION_INVENTOR_PARTICIPACION_NO_DELETABLE = marker('pii.invencion-inventor.error.no-deletable');
const MSG_DELETE = marker('msg.delete.entity');

@Component({
  selector: 'sgi-invencion-inventor',
  templateUrl: './invencion-inventor.component.html',
  styleUrls: ['./invencion-inventor.component.scss']
})
export class InvencionInventorComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: InvencionInventorFragment;

  msgParamEntity = {};
  msgErrorParticipacion: string;
  msgConfirmDelete: string;
  msgNoDeletable: string;

  columnas = ['nombre', 'apellidos', 'persona', 'entidad', 'participacion', 'esReparto', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  dataSource = new MatTableDataSource<StatusWrapper<IInvencionInventor>>();
  isInvecionInvtoresEmpty: boolean;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    readonly actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private invencionInventoriService: InvencionInventorService
  ) {
    super(actionService.FRAGMENT.INVENCION_INVENTOR, actionService);
    this.formPart = this.fragment as InvencionInventorFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IInvencionInventor>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.inventor.nombre;
          case 'apellidos':
            return wrapper.value.inventor.apellidos;
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.inventor);
          case 'esReparto':
            return wrapper.value.repartoUniversidad;
          case 'participacion':
            return wrapper.value.participacion;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.subscriptions.push(
      this.formPart.invencionInventores$
        .pipe(
          tap(elements => this.isInvecionInvtoresEmpty = elements.length === 0)
        )
        .subscribe(elements => {
          this.dataSource.data = elements;
        })
    );

  }

  private setupI18N(): void {

    this.translate.get(
      INVENCION_INVENTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      INVENCION_INVENTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          INVENCION_INVENTOR_PARTICIPACION_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.msgErrorParticipacion = value);

    this.translate.get(
      INVENCION_INVENTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.msgConfirmDelete = value);

    this.translate.get(
      INVENCION_INVENTOR_PARTICIPACION_NO_DELETABLE
    ).subscribe((value) => this.msgNoDeletable = value);

  }

  openModal(invencionInventor: StatusWrapper<IInvencionInventor>): void {

    const modalData$ = forkJoin([this.formPart.invencionInventores$.pipe(take(1)), of(invencionInventor)]).pipe(
      map(([inventores, inventor]) => {
        const editionMode = inventor != null;
        const invencionInventorWrapped = new StatusWrapper<IInvencionInventor>({} as IInvencionInventor);
        invencionInventorWrapped.setCreated();
        const invencionInventorModalData: InvencionInventorModalData = {
          invencionInventor: editionMode ? inventor : invencionInventorWrapped,
          isEdit: editionMode,
          inventoresNotAllowed: inventores.map(elem => elem.value.inventor)
        };
        return invencionInventorModalData;
      })
    );

    this.subscriptions.push(modalData$.pipe(
      switchMap(data => {
        const config: MatDialogConfig = {
          data
        };
        return this.matDialog.open(InvencionInventorModalComponent, config).afterClosed();
      }),
      take(1),
      filter(inventor => inventor !== '' && inventor !== void 0)
    ).subscribe(
      (invencionInventorWrapped: StatusWrapper<IInvencionInventor>) => {
        switch (invencionInventorWrapped.status) {
          case Status.EDITED:
            this.formPart.editInvencionInventor(invencionInventorWrapped);
            break;
          case Status.CREATED:
            this.formPart.addInvencionInventor(invencionInventorWrapped);
            break;
          default:
            break;
        }
      }
    )
    );

  }

  deleteInventor = (invencionInventor: StatusWrapper<IInvencionInventor>) => {
    const eliminable$ = invencionInventor.value?.id ? this.invencionInventoriService.isEliminable(invencionInventor.value.id) : of(true);

    this.subscriptions.push(
      eliminable$.pipe(
        switchMap(isEliminable => {
          if (!isEliminable) {
            return this.dialogService.showInfoDialog(this.msgNoDeletable);
          }

          return this.dialogService.showConfirmation(this.msgConfirmDelete).pipe(
            filter(accepted => !!accepted),
            tap(() => this.formPart.deleteInvencionInventor(invencionInventor))
          )
        })
      ).subscribe()
    );
  }

  ngOnDestroy = () => this.subscriptions.forEach(subscription =>
    subscription.unsubscribe())

}
