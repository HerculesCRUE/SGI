import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IComentario, TipoEstadoComentario } from '@core/models/eti/comentario';
import { TIPO_COMENTARIO, TipoComentario } from '@core/models/eti/tipo-comentario';
import { DialogService } from '@core/services/dialog.service';
import { TipoComentarioService } from '@core/services/eti/tipo-comentario.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ComentarioModalComponent, ComentarioModalData } from '../../comentario/comentario-modal/comentario-modal.component';
import { getApartadoNombre, getSubApartadoNombre } from '../../shared/pipes/bloque-apartado.pipe';
import { Rol, SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoComentarioFragment } from './seguimiento-comentarios.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const COMENTARIO_KEY = marker('eti.comentario');

@Component({
  selector: 'sgi-seguimiento-comentarios',
  templateUrl: './seguimiento-comentarios.component.html',
  styleUrls: ['./seguimiento-comentarios.component.scss']
})
export class SeguimientoComentariosComponent extends FragmentComponent implements OnInit, OnDestroy {
  public formPart: SeguimientoComentarioFragment;
  private subscriptions: Subscription[] = [];
  tipoComentario$: Observable<TipoComentario>;

  columnas: string[];
  elementosPagina: number[];

  msgParamEntity: {};

  public personaId: string;
  public disabledCreate = false;

  dataSource: MatTableDataSource<StatusWrapper<IComentario>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  textoDelete: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_ESTADO_COMENTARIO() {
    return TipoEstadoComentario;
  }

  constructor(
    private readonly dialogService: DialogService,
    private tipoComentarioService: TipoComentarioService,
    private matDialog: MatDialog,
    private actionService: SeguimientoFormularioActionService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService
  ) {
    super(actionService.FRAGMENT.COMENTARIOS, actionService);
    this.dataSource = new MatTableDataSource<StatusWrapper<IComentario>>();
    this.formPart = this.fragment as SeguimientoComentarioFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.personaId = this.authService.authStatus$.value.userRefId;
    this.columnas = ['evaluador.nombre', 'apartado.bloque', 'apartado.padre',
      'apartado', 'texto', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.comentarios$.subscribe(elements => {
      this.dataSource.data = elements;
      if (elements.length > 0 && elements.filter(comentario => comentario.value.evaluador.id === this.personaId).length > 0) {
        this.disabledCreate = !elements.some(comentario => comentario.value.estado ? (comentario.value.estado === this.TIPO_ESTADO_COMENTARIO.ABIERTO || comentario.value.estado === null) : true);
      }
    }));

    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IComentario>, property: string) => {
        switch (property) {
          case 'apartado.bloque':
            return wrapper.value.apartado?.bloque.nombre;
          case 'apartado.padre':
            return this.getApartadoNombre(wrapper.value);
          case 'apartado':
            return this.getSubApartadoNombre(wrapper.value);
          default:
            return wrapper.value[property];
        }
      };

  }

  private setupI18N(): void {
    this.translate.get(
      COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getApartadoNombre(comentario: IComentario): string {
    return getApartadoNombre(comentario.apartado);
  }

  getSubApartadoNombre(comentario: IComentario): string {
    return getSubApartadoNombre(comentario.apartado);
  }

  /**
   * Abre la ventana modal para añadir un comentario
   */
  openCreateModal(): void {
    const evaluacionData: ComentarioModalData = {
      evaluaciones: [this.actionService.getEvaluacion()],
      comentario: undefined,
      readonly: false
    };

    const config = {
      data: evaluacionData
    };
    const dialogRef = this.matDialog.open(ComentarioModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ComentarioModalData) => {
        if (modalData && modalData.comentario) {
          this.formPart.addComentario(modalData.comentario);
        }
      }
    );
  }

  /**
   * Abre la ventana modal para modificar un comentario
   *
   * @param comentario Comentario a modificar
   */
  openEditModal(comentario: StatusWrapper<IComentario>): void {
    const wrapperRef = comentario;

    const evaluacionData: ComentarioModalData = {
      evaluaciones: [this.actionService.getEvaluacion()],
      comentario: wrapperRef.value,
      readonly: !this.isEditable(wrapperRef)
    };

    const config = {
      data: evaluacionData
    };

    const dialogRef = this.matDialog.open(ComentarioModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ComentarioModalData) => {
        if (modalData && modalData.comentario) {
          this.formPart.deleteComentario(wrapperRef);
          this.subscriptions.push(this.getTipoComentario().subscribe(
            (tipoComentario) => {
              modalData.comentario.tipoComentario = tipoComentario;
              this.formPart.addComentario(modalData.comentario);
            }
          ));
        }
      }
    );
  }

  /**
   * Elimina un comentario del listado
   *
   * @param comentario Comentario a eliminar
   */
  deleteComentario(comentario: StatusWrapper<IComentario>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteComentario(comentario);
          }
        }
      )
    );
  }

  getTipoComentario(): Observable<TipoComentario> {
    if (this.actionService.getRol() === Rol.GESTOR) {
      this.tipoComentario$ = this.tipoComentarioService.findById(1);
    } else {
      this.tipoComentario$ = this.tipoComentarioService.findById(2);
    }
    return this.tipoComentario$;
  }

  isTipoGestor(comentario: IComentario): boolean {
    return comentario.tipoComentario.id === TIPO_COMENTARIO.GESTOR;
  }

  isEditable(wrapperComentario: StatusWrapper<IComentario>): boolean {
    return (wrapperComentario.value.estado === undefined || (this.isTipoGestor(wrapperComentario.value) || (wrapperComentario.value.estado === this.TIPO_ESTADO_COMENTARIO.ABIERTO && this.personaId === wrapperComentario.value.evaluador?.id)))
  }
}
