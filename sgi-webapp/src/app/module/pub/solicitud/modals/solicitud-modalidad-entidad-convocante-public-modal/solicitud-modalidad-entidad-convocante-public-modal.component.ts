import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ProgramaPublicService } from '@core/services/csp/programa-public.service';
import { TranslateService } from '@ngx-translate/core';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const SOLICITUD_MODALIDAD_KEY = marker('csp.solicitud-modalidad');

export interface SolicitudModalidadEntidadConvocantePublicModalData {
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
  templateUrl: './solicitud-modalidad-entidad-convocante-public-modal.component.html',
  styleUrls: ['./solicitud-modalidad-entidad-convocante-public-modal.component.scss']
})
export class SolicitudModalidadEntidadConvocantePublicModalComponent
  extends DialogFormComponent<SolicitudModalidadEntidadConvocantePublicModalData> implements OnInit {

  title: string;

  checkedNode: NodePrograma;
  rootNode: NodePrograma;

  dataSource = new MatTreeNestedDataSource<NodePrograma>();
  nodeMap = new Map<number, NodePrograma>();
  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);

  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: SolicitudModalidadEntidadConvocantePublicModalData,
    matDialogRef: MatDialogRef<SolicitudModalidadEntidadConvocantePublicModalComponent>,
    private programaService: ProgramaPublicService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.modalidad);
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
    this.translate.get(
      SOLICITUD_MODALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);
  }


  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      entidadConvocante: new FormControl({ value: this.data.entidad?.nombre, disabled: true }),
      plan: new FormControl({ value: this.data.plan?.nombre, disabled: true }),
      modalidad: new FormControl(this.data.modalidad?.programa)
    });

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getValue(): SolicitudModalidadEntidadConvocantePublicModalData {
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
    this.formGroup.get('modalidad').setValue(this.checkedNode?.programa);
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
