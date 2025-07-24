import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IComentario, TipoEstadoComentario } from '@core/models/eti/comentario';
import { TIPO_COMENTARIO, TipoComentario } from '@core/models/eti/tipo-comentario';
import { DialogService } from '@core/services/dialog.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { TipoComentarioService } from '@core/services/eti/tipo-comentario.service';
import { LanguageService } from '@core/services/language.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ComentarioModalComponent, ComentarioModalData } from '../../../comentario/comentario-modal/comentario-modal.component';
import { getApartadoNombre, getSubApartadoNombre } from '../../../shared/pipes/bloque-apartado.pipe';
import { Rol } from '../../acta-rol';
import { ActaActionService } from '../../acta.action.service';
import { ActaComentariosFragment } from './acta-comentarios.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const COMENTARIO_KEY = marker('eti.comentario');

@Component({
  selector: 'sgi-acta-comentarios',
  templateUrl: './acta-comentarios.component.html',
  styleUrls: ['./acta-comentarios.component.scss']
})
export class ActaComentariosComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ActaComentariosFragment;
  private subscriptions: Subscription[] = [];

  columnas: string[];
  elementosPagina: number[];
  tipoComentario$: Observable<TipoComentario>;

  dataSource: MatTableDataSource<StatusWrapper<IComentario>> = new MatTableDataSource<StatusWrapper<IComentario>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  textoDelete: string;
  msgParamComentarioEntity = {};

  public personaId: string;
  public disabledCreate = false;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get disabledButtonComentarios() {
    if (this.actionService.getLengthMemorias() > 0) {
      return !this.formPart.showAddComentarios;
    } else {
      return true;
    }
  }

  get readonly() {
    return this.actionService.readonly;
  }

  get TIPO_ESTADO_COMENTARIO() {
    return TipoEstadoComentario;
  }

  constructor(
    private readonly dialogService: DialogService,
    private tipoComentarioService: TipoComentarioService,
    private matDialog: MatDialog,
    private actionService: ActaActionService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService,
    private readonly bloqueService: BloqueService,
    private readonly router: Router,
    private readonly languageService: LanguageService
  ) {
    super(actionService.FRAGMENT.COMENTARIOS, actionService, translate);
    this.personaId = this.authService.authStatus$.value.userRefId;
    this.formPart = this.fragment as ActaComentariosFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['evaluador.nombre', 'memoria.numReferencia', 'apartado.bloque', 'apartado.padre',
      'apartado', 'texto', 'acciones'];

  }

  ngOnInit() {
    super.ngOnInit();

    this.actionService.initializeMemorias();
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
            return this.getBloqueNombre(wrapper.value);
          case 'apartado.padre':
            return this.getApartadoNombre(wrapper.value);
          case 'apartado':
            return this.getSubApartadoNombre(wrapper.value);
          case 'texto':
            return this.languageService.getFieldValue(wrapper.value.texto);
          default:
            return wrapper.value[property];
        }
      };
  }

  protected setupI18N(): void {
    this.translate.get(
      COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

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
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getApartadoNombre(comentario: IComentario): string {
    return getApartadoNombre(comentario.apartado, this.languageService.getLanguage());
  }

  getSubApartadoNombre(comentario: IComentario): string {
    return getSubApartadoNombre(comentario.apartado, this.languageService.getLanguage());
  }

  getBloqueNombre(comentario: IComentario): string {
    return comentario.apartado?.bloque.orden === 0 ?
      this.languageService.getFieldValue(comentario.apartado?.bloque.nombre) : (comentario.apartado?.bloque?.orden
        + ' ' + this.languageService.getFieldValue(comentario.apartado?.bloque.nombre))
  }

  /**
   * Abre la ventana modal para añadir un comentario
   */
  openCreateModal(): void {
    this.subscriptions.push(this.formPart.evaluaciones$.subscribe(evaluaciones => {
      const actaData: ComentarioModalData = {
        evaluaciones,
        comentario: undefined,
        readonly: false,
      };

      const config = {
        data: actaData
      };
      const dialogRef = this.matDialog.open(ComentarioModalComponent, config);
      dialogRef.afterClosed().subscribe(
        (modalData: ComentarioModalData) => {
          if (modalData && modalData.comentario) {
            this.formPart.addComentario(modalData.comentario);
          }
        }
      );
    }));
  }

  /**
   * Abre la ventana modal para modificar un comentario
   *
   * @param comentario Comentario a modificar
   */
  openEditModal(comentario: StatusWrapper<IComentario>): void {
    this.subscriptions.push(this.formPart.evaluaciones$.subscribe(evaluaciones => {
      const wrapperRef = comentario;

      const actaData: ComentarioModalData = {
        evaluaciones,
        comentario: wrapperRef.value,
        readonly: !this.isEditable(wrapperRef)
      };

      const config = {
        data: actaData
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
    }));
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
    return comentario.tipoComentario.id === TIPO_COMENTARIO.ACTA_GESTOR;
  }

  isEditable(wrapperComentario: StatusWrapper<IComentario>): boolean {
    return (wrapperComentario.value.estado === undefined || (this.isTipoGestor(wrapperComentario.value) || (wrapperComentario.value.estado === this.TIPO_ESTADO_COMENTARIO.ABIERTO && this.personaId === wrapperComentario.value.evaluador?.id))) && !this.readonly;
  }

}
