import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { IComentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ActionService } from '@core/services/action-service';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { EvaluacionFormularioActionService } from '../../evaluacion-formulario/evaluacion-formulario.action.service';

const MSG_ERROR_APARTADO = marker('error.load');
const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const COMENTARIO_KEY = marker('eti.comentario');
const COMENTARIO_BLOQUE_KEY = marker('eti.comentario.bloque');
const BTN_ADD = marker('btn.add');
const BTN_OK = marker('btn.ok');

export interface ComentarioModalData {
  evaluacion: IEvaluacion;
  comentario: IComentario;
}

class NodeApartado {
  parent: NodeApartado;
  apartado: StatusWrapper<IApartado>;
  // tslint:disable-next-line: variable-name
  _childs: NodeApartado[];
  get childs(): NodeApartado[] {
    return this._childs;
  }

  constructor(apartado: StatusWrapper<IApartado>) {
    this.apartado = apartado;
    this._childs = [];
  }

  addChild(child: NodeApartado) {
    child.parent = this;
    child.apartado.value.padre = this.apartado.value;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodeApartado) {
    this._childs = this._childs.filter((apartado) => apartado !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodeApartado[]): NodeApartado[] {
  return nodes.sort((a, b) => {
    if (a.apartado.value.nombre < b.apartado.value.nombre) {
      return -1;
    }
    if (a.apartado.value.nombre > b.apartado.value.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './comentario-modal.component.html',
  styleUrls: ['./comentario-modal.component.scss'],
  providers: [
    {
      provide: ActionService,
      useExisting: EvaluacionFormularioActionService
    }
  ]
})
export class ComentarioModalComponent extends
  BaseModalComponent<ComentarioModalData, ComentarioModalComponent> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;

  apartado$: Observable<IBloque[]>;
  treeControl = new NestedTreeControl<NodeApartado>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodeApartado>();
  private nodeMap = new Map<number, NodeApartado>();

  mostrarError: boolean;

  checkedNode: NodeApartado;
  hasChild = (_: number, node: NodeApartado) => node.childs.length > 0;

  msgParamBloqueEntity = {};
  msgParamComentarioEntity = {};
  title: string;
  textoAceptar: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private logger: NGXLogger,
    private formularioService: FormularioService,
    private bloqueService: BloqueService,
    private apartadoService: ApartadoService,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ComentarioModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ComentarioModalData,
    private translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
    this.fxLayoutProperties.layoutAlign = 'space-around start';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadBloques();
    if (this.data?.comentario?.apartado?.bloque) {
      this.loadTreeApartados(this.data?.comentario?.apartado?.bloque.id);
    }
    const subscription = this.formGroup.get('bloque').valueChanges.subscribe((value) => this.loadTreeApartados(value.id));
    this.subscriptions.push(subscription);
  }

  private setupI18N(): void {
    this.translate.get(
      COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      COMENTARIO_BLOQUE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamBloqueEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.comentario) {
      this.translate.get(
        COMENTARIO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.translate.get(
        BTN_OK,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.textoAceptar = value);
    } else {
      this.translate.get(
        COMENTARIO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);

      this.translate.get(
        BTN_ADD,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.textoAceptar = value);
    }
  }

  /**
   * Carga todos los bloques de la aplicación
   */
  private loadBloques(): void {
    this.apartado$ = this.formularioService.getBloques(resolveFormularioByTipoEvaluacionAndComite
      (this.data?.evaluacion?.tipoEvaluacion?.id, this.data?.evaluacion?.memoria?.comite)).pipe(
        map(res => res.items),
        catchError(error => {
          this.matDialogRef.close(null);
          this.snackBarService.showError(MSG_ERROR_APARTADO);
          this.logger.error(ComentarioModalComponent.name,
            `loadBloques()`, error);
          return of([]);
        })
      );
  }

  /**
   * Carga todos los apartados del bloque seleccionado
   */
  private loadTreeApartados(id?: number) {
    this.nodeMap.clear();
    this.dataSource.data = [];
    if (id) {
      const susbcription = this.bloqueService.getApartados(id).pipe(
        switchMap(response => {
          return from(response.items).pipe(
            mergeMap((apartado) => {
              const node = new NodeApartado(new StatusWrapper<IApartado>(apartado));
              this.nodeMap.set(node.apartado.value.id, node);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (result) => {
          const current = this.dataSource.data ? this.dataSource.data : [];
          current.push(result);
          this.publishNodes(current);
          this.checkedNode = this.nodeMap.get(this.data.evaluacion.tipoEvaluacion.id);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        },
        (error) => {
          this.logger.error(ComentarioModalComponent.name,
            `loadTreeApartados()`, error);
        }
      );
      this.subscriptions.push(susbcription);
    }
  }

  /**
   * Carga todos los subapartados del apartado seleccionado en el formulario
   */
  private getChilds(parent: NodeApartado): Observable<NodeApartado[]> {
    return this.apartadoService.getHijos(parent.apartado.value.id).pipe(
      map((result) => {
        const childs: NodeApartado[] = result.items.map(
          (apartado) => {
            const child = new NodeApartado(new StatusWrapper<IApartado>(apartado));
            child.parent = parent;
            this.nodeMap.set(child.apartado.value.id, child);
            return child;
          });
        return childs;
      }),
      switchMap((nodes) => {
        parent.childs.push(...nodes);
        parent.sortChildsByName();
        if (nodes.length > 0) {
          return from(nodes).pipe(
            mergeMap((node) => {
              return this.getChilds(node);
            })
          );
        }
        else {
          return of([]);
        }
      }),
      takeLast(1)
    );
  }

  private expandNodes(node: NodeApartado) {
    if (node && node.parent) {
      this.treeControl.expand(node.parent);
      this.expandNodes(node.parent);
    }
  }

  private publishNodes(rootNodes?: NodeApartado[]) {
    let nodes = rootNodes ? rootNodes : this.dataSource.data;
    nodes = sortByName(nodes);
    this.dataSource.data = nodes;
  }

  onCheckNode(node: NodeApartado, $event: MatCheckboxChange): void {
    this.mostrarError = false;
    this.checkedNode = $event.checked ? node : undefined;
  }

  getNombreBloque(bloque: IBloque): string {
    return bloque?.nombre;
  }

  /**
   * Comprueba el formulario y envia el comentario resultante
   */
  saveOrUpdate() {
    if (this.formGroup.valid) {
      if (this.checkedNode?.apartado?.value) {
        this.matDialogRef.close(this.getDatosForm());
      } else {
        this.mostrarError = true;
        this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
      }
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      bloque: new FormControl(this.data?.comentario?.apartado?.bloque, [Validators.required, IsEntityValidator.isValid()]),
      comentario: new FormControl(this.data?.comentario?.texto, [
        Validators.required, Validators.maxLength(2000)])
    });

    return formGroup;
  }

  /**
   * Método para actualizar la entidad con los datos del formgroup + tree
   */
  protected getDatosForm(): ComentarioModalData {
    const comentario = {} as IComentario;
    const subapartado: IApartado = this.checkedNode?.apartado?.value;
    if (subapartado) {
      comentario.apartado = subapartado;
    }
    comentario.texto = FormGroupUtil.getValue(this.formGroup, 'comentario');
    comentario.memoria = this.data.evaluacion.memoria;
    this.data.comentario = comentario;
    return this.data;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}
