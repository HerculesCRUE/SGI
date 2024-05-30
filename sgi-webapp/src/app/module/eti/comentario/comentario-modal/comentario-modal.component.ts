import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { IComentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { ActionService } from '@core/services/action-service';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, forkJoin, from, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { EvaluacionFormularioActionService } from '../../evaluacion-formulario/evaluacion-formulario.action.service';

const TITLE_NEW_ENTITY = marker('title.new.entity');
const COMENTARIO_KEY = marker('eti.comentario');
const COMENTARIO_BLOQUE_KEY = marker('eti.comentario.bloque');
const BTN_ADD = marker('btn.add');
const BTN_OK = marker('btn.ok');
const MEMORIA_KEY = marker('eti.memoria');

export interface ComentarioModalData {
  evaluaciones: IEvaluacion[];
  comentario: IComentario;
  readonly: boolean;
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
    this.sortChildsByOrden();
  }

  removeChild(child: NodeApartado) {
    this._childs = this._childs.filter((apartado) => apartado !== child);
  }

  sortChildsByOrden(): void {
    this._childs = sortByOrden(this._childs);
  }
}

function sortByOrden(nodes: NodeApartado[]): NodeApartado[] {
  return nodes.sort((a, b) => {
    if (a.apartado.value.orden < b.apartado.value.orden) {
      return -1;
    }
    if (a.apartado.value.orden > b.apartado.value.orden) {
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
export class ComentarioModalComponent extends DialogFormComponent<ComentarioModalData> implements OnInit {

  bloques$: Observable<IBloque[]>;
  evaluaciones$: Observable<IEvaluacion[]>;
  treeControl = new NestedTreeControl<NodeApartado>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodeApartado>();
  private nodeMap = new Map<number, NodeApartado>();

  public readonly = false;

  // tslint:disable-next-line: variable-name
  private _checkedNode: NodeApartado;
  get checkedNode(): NodeApartado {
    return this._checkedNode;
  }
  set checkedNode(value: NodeApartado) {
    this._checkedNode = value;
    this.formGroup?.controls.apartado.setValue(value?.apartado?.value);
  }
  hasChild = (_: number, node: NodeApartado) => node.childs.length > 0;

  msgParamBloqueEntity = {};
  msgParamComentarioEntity = {};
  msgParamMemoriaEntity = {};
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
    matDialogRef: MatDialogRef<ComentarioModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ComentarioModalData,
    private translate: TranslateService
  ) {
    super(matDialogRef, !!data.comentario);
    if (this.data?.comentario) {
      this.readonly = this.data.readonly;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadMemorias();
    if (this.data?.comentario?.apartado?.bloque) {
      this.loadTreeApartados(this.data?.comentario?.apartado?.bloque.id);
    }
    this.subscriptions.push(this.formGroup.get('bloque').valueChanges.subscribe((value) => this.loadTreeApartados(value.id)));
    if (this.data?.evaluaciones?.length > 1) {
      this.subscriptions.push(this.formGroup.get('evaluacion').valueChanges.subscribe((value) => this.loadBloques(value)));
    } else {
      this.loadBloques(this.data?.evaluaciones[0]);
    }
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

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMemoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

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
   * Carga todas las evaluaciones del acta
   */
  private loadMemorias(): void {
    this.evaluaciones$ = of(this.data.evaluaciones);
  }

  /**
   * Carga todos los bloques de la aplicación
   */
  private loadBloques(evaluacion: IEvaluacion): void {
    this.bloques$ = forkJoin({
      bloquesFormulario: this.formularioService.getBloques(resolveFormularioByTipoEvaluacionAndComite
        (evaluacion?.tipoEvaluacion?.id, evaluacion?.memoria?.comite)),
      bloqueComentariosGenerales: this.bloqueService.getBloqueComentariosGenerales()
    }).pipe(
      map(({ bloquesFormulario, bloqueComentariosGenerales }) => bloquesFormulario.items.concat([bloqueComentariosGenerales])),
      catchError(error => {
        this.processError(error);
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
          if (this.data.comentario?.apartado) {
            this.checkedNode = this.nodeMap.get(this.data.comentario.apartado.id);
            if (this.checkedNode) {
              this.expandNodes(this.checkedNode);
            }
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
        parent.sortChildsByOrden();
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
    nodes = sortByOrden(nodes);
    this.dataSource.data = nodes;
  }

  onCheckNode(node: NodeApartado, $event: MatCheckboxChange): void {
    this.checkedNode = $event.checked ? node : undefined;
  }

  getNombreBloque(bloque: IBloque): string {
    return bloque?.nombre;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      bloque: new FormControl(this.data?.comentario?.apartado?.bloque, [Validators.required]),
      apartado: new FormControl(this.data.comentario?.apartado, [Validators.required]),
      comentario: new FormControl(this.data?.comentario?.texto, [
        Validators.required, Validators.maxLength(2000)]),
      evaluacion: new FormControl(this.data?.evaluaciones?.length === 1 ? this.data?.evaluaciones[0] : this.data?.comentario ? this.data?.evaluaciones.filter(ev => ev.memoria.id === this.data.comentario?.memoria?.id)[0] : null, [Validators.required, IsEntityValidator.isValid()])
    });

    if (this.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  /**
   * Método para actualizar la entidad con los datos del formgroup + tree
   */
  protected getValue(): ComentarioModalData {
    const comentario = {} as IComentario;
    const subapartado: IApartado = this.checkedNode?.apartado?.value;
    if (subapartado) {
      comentario.apartado = subapartado;
    }
    comentario.apartado = this.formGroup.get('apartado').value;
    comentario.texto = this.formGroup.get('comentario').value;
    comentario.memoria = this.formGroup.get('evaluacion').value?.memoria;
    this.data.comentario = comentario;
    return this.data;
  }

  displayerMemoria(evaluacion: IEvaluacion): string {
    return evaluacion.memoria?.numReferencia;
  }

  displayerBloque(bloque: IBloque): string {
    return bloque.orden + ' ' + bloque.nombre;
  }

}
