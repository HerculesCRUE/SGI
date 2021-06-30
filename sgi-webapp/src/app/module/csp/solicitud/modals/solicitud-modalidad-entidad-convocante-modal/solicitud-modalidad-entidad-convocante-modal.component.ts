import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProgramaService } from '@core/services/csp/programa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');
const SOLICITUD_MODALIDAD_KEY = marker('csp.solicitud-modalidad');
export interface SolicitudModalidadEntidadConvocanteModalData {
  entidad: IEmpresa;
  plan: IPrograma;
  programa: IPrograma;
  modalidad: ISolicitudModalidad;
  readonly: boolean;
}

class NodePrograma {
  parent: NodePrograma;
  programa: IPrograma;
  // tslint:disable-next-line: variable-name
  _childs: NodePrograma[];
  get childs(): NodePrograma[] {
    return this._childs;
  }

  constructor(programa: IPrograma) {
    this.programa = programa;
    this._childs = [];
  }

  addChild(child: NodePrograma) {
    child.parent = this;
    child.programa.padre = this.programa;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodePrograma) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodePrograma[]): NodePrograma[] {
  return nodes.sort((a, b) => {
    if (a.programa.nombre < b.programa.nombre) {
      return -1;
    }
    if (a.programa.nombre > b.programa.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './solicitud-modalidad-entidad-convocante-modal.component.html',
  styleUrls: ['./solicitud-modalidad-entidad-convocante-modal.component.scss']
})
export class SolicitudModalidadEntidadConvocanteModalComponent
  extends BaseModalComponent<SolicitudModalidadEntidadConvocanteModalData, SolicitudModalidadEntidadConvocanteModalComponent>
  implements OnInit {

  fxFlexPropertiesTree: FxFlexProperties;

  isEdit = false;
  title: string;

  checkedNode: NodePrograma;
  rootNode: NodePrograma;

  dataSource = new MatTreeNestedDataSource<NodePrograma>();
  nodeMap = new Map<number, NodePrograma>();
  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);

  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudModalidadEntidadConvocanteModalData,
    public readonly matDialogRef: MatDialogRef<SolicitudModalidadEntidadConvocanteModalComponent>,
    private programaService: ProgramaService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.isEdit = data.modalidad ? true : false;

    this.fxFlexPropertiesTree = new FxFlexProperties();
    this.fxFlexPropertiesTree.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesTree.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesTree.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesTree.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    const subscription = this.getTreePrograma(this.data.programa, this.data.modalidad?.programa)
      .subscribe((nodePrograma) => {
        this.dataSource.data = [nodePrograma];
      });

    this.subscriptions.push(subscription);
  }

  private setupI18N(): void {
    if (this.isEdit) {
      this.translate.get(
        SOLICITUD_MODALIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        SOLICITUD_MODALIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }


  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      entidadConvocante: new FormControl({ value: this.data.entidad?.nombre, disabled: true }),
      plan: new FormControl({ value: this.data.plan?.nombre, disabled: true })
    });

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getDatosForm(): SolicitudModalidadEntidadConvocanteModalData {
    const entidadConvocanteModalidad = this.data;

    if (!this.checkedNode) {
      entidadConvocanteModalidad.modalidad = null;
      return entidadConvocanteModalidad;
    }

    if (!entidadConvocanteModalidad.modalidad) {
      entidadConvocanteModalidad.modalidad = {
        entidad: entidadConvocanteModalidad.entidad
      } as ISolicitudModalidad;
    }

    entidadConvocanteModalidad.modalidad.programa = this.checkedNode?.programa;
    return entidadConvocanteModalidad;
  }

  /**
   * Carga el arbol que tiene como raiz el programa rootPrograma y si se indica el checkedPrograma
   * se marca como seleccionado dicho nodo del arbol.
   *
   * @param rootPrograma el programa raiz del arbol.
   * @param checkedPrograma el programa seleccionado en el arbol.
   */
  getTreePrograma(rootPrograma: IPrograma, checkedPrograma?: IPrograma): Observable<NodePrograma> {
    const node = new NodePrograma(rootPrograma);
    this.rootNode = node;
    this.nodeMap.set(node.programa.id, node);
    return this.getChilds(node).pipe(map(() => node)).pipe(
      tap(() => {
        this.checkedNode = this.nodeMap.get(checkedPrograma?.id);
        if (this.checkedNode) {
          this.expandParentNodes(this.checkedNode);
        }
      })
    );
  }

  /**
   * Actualiza el nodo del arbol que esta seleccionado.
   */
  onCheckNode(node: NodePrograma, $event: MatCheckboxChange): void {
    this.checkedNode = $event.checked ? node : undefined;
  }

  /**
   * Obtiene los hijos del nodo recursivamente hasta llegar a las "hojas"
   *
   * @param parent nodo del que se quieren obtener los hijos
   * @returns observable con el arbol de nodos con parent como nodo raiz.
   */
  private getChilds(parent: NodePrograma): Observable<NodePrograma[]> {
    return this.programaService.findAllHijosPrograma(parent.programa.id).pipe(
      map((result) => {
        const childs: NodePrograma[] = result.items.map(
          (programa) => {
            const child = new NodePrograma(programa);
            child.parent = parent;
            this.nodeMap.set(child.programa.id, child);
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
        return of([]);
      }),
      takeLast(1)
    );
  }

  /**
   * Expande todos los nodos desde el nodo indicado hasta la raiz del arbol.
   *
   * @param node nodo del arbol
   */
  private expandParentNodes(node: NodePrograma): void {
    if (!node || !node.parent) {
      return;
    }

    this.treeControl.expand(node.parent);
    this.expandParentNodes(node.parent);
  }

}
