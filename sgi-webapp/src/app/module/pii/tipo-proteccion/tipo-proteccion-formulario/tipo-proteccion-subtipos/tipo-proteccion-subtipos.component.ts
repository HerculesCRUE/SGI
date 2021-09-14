import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { DialogService } from '@core/services/dialog.service';
import { Status, StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { TipoProteccionSubtipoModalComponent } from '../../modals/tipo-proteccion-subtipo-modal/tipo-proteccion-subtipo-modal.component';
import { TipoProteccionActionService } from '../../tipo-proteccion.action.service';
import { TipoProteccionSubtiposFragment } from './tipo-proteccion-subtipos.fragment';

const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const SUBTIPO_PROTECCION_KEY = marker('pii.subtipo-proteccion');
const SUBTIPO_PROTECCION_NOMBRE_DUPLICADO_KEY = marker('pii.subtipo-proteccion.nombre.duplicado');
const SUBTIPO_PROTECCION_CREATE_SUCCESS = marker('msg.save.entity.success');
const SUBTIPO_PROTECCION_CREATE_ERROR = marker('error.save.entity');
const SUBTIPO_PROTECCION_EDIT_SUCCESS = marker('msg.update.entity.success');
const SUBTIPO_PROTECCION_EDIT_ERROR = marker('error.update.entity');
const SUBTIPO_PROTECCION_DEACTIVATE_SUCCESS = marker('msg.deactivate.entity.success');
const SUBTIPO_PROTECCION_DEACTIVATE_ERROR = marker('error.deactivate.entity');
const SUBTIPO_PROTECCION_REACTIVATE_SUCCESS = marker('msg.reactivate.entity.success');
const SUBTIPO_PROTECCION_REACTIVATE_ERROR = marker('error.reactivate.entity');

@Component({
  selector: 'sgi-tipo-proteccion-subtipos',
  templateUrl: './tipo-proteccion-subtipos.component.html',
  styleUrls: ['./tipo-proteccion-subtipos.component.scss']
})
export class TipoProteccionSubtiposComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: TipoProteccionSubtiposFragment;

  elementosPagina = [5, 10, 25, 100];
  columnas = ['nombre', 'descripcion', 'activo', 'acciones'];

  msgParamEntity = {};
  textoErrorNombreDuplicado: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoExitoCrear: string;
  textoErrorCrear: string;
  textoExitoEditar: string;
  textoErrorEditar: string;
  textoSuccessDeactivate: string;
  textoErrorDeactivate: string;
  textoSuccessReactivate: string;
  textoErrorReactivate: string;


  dataSource = new MatTableDataSource<StatusWrapper<ITipoProteccion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    readonly actionService: TipoProteccionActionService,
    private readonly matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.SUBTIPOS, actionService);

    this.formPart = this.fragment as TipoProteccionSubtiposFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.subtiposProteccion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

  }

  private setupI18N(): void {
    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_NOMBRE_DUPLICADO_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorNombreDuplicado = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_CREATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoExitoCrear = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_EDIT_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoExitoEditar = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_CREATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorCrear = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_EDIT_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorEditar = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_DEACTIVATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDeactivate = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_DEACTIVATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDeactivate = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_REACTIVATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivate = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          SUBTIPO_PROTECCION_REACTIVATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivate = value);

  }

  /**
   * Apertura de modal de {@link ITipoProteccion}
   */
  openModal(subtipoProteccion: StatusWrapper<ITipoProteccion>): void {
    const subtipoProteccionToModify = subtipoProteccion != null ?
      subtipoProteccion : this.formPart.createEmptySubtipoProteccion();

    const config = {
      panelClass: 'sgi-dialog-container',
      data: subtipoProteccionToModify
    };
    const dialogRef = this.matDialog.open(TipoProteccionSubtipoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: StatusWrapper<ITipoProteccion>) => {
        switch (result.status) {
          case Status.CREATED:
            this.formPart.agregarSubtipoProteccion(result, this.textoErrorNombreDuplicado, this.textoErrorCrear);
            break;
        }
      });
  }

  private showConfirmationMessage(message: string, callback) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message)
        .pipe(
          filter(response => !!response)
        )
        .subscribe(
          () => callback()
        )
    );
  }

  /**
   * Elimina el {@link ITipoProteccion}
   *
   * @param wrapper del {@link ITipoProteccion}
   */
  deactivateTipoProteccion(wrapper: StatusWrapper<ITipoProteccion>) {
    this.showConfirmationMessage(this.textoDesactivar, () => {
      this.formPart.deactivateTipoProteccion(wrapper);
    });
  }

  /**
   * Recupera el {@link ITipoProteccion}
   *
   * @param wrapper del {@link ITipoProteccion}
   */
  activateTipoProteccion(wrapper: StatusWrapper<ITipoProteccion>) {
    this.showConfirmationMessage(this.textoReactivar, () => {
      this.formPart.activateTipoProteccion(wrapper);
    });
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
