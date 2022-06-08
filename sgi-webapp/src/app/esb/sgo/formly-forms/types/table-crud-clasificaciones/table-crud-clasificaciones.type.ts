import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { TranslateService } from '@ngx-translate/core';
import { ClasificacionDataModal, ClasificacionModalComponent, TipoClasificacion } from 'src/app/esb/sgo/shared/clasificacion-modal/clasificacion-modal.component';
import { BaseTableCRUDTypeComponent, TYPE_RENDER_COLUMN } from 'src/app/esb/shared/formly-forms/types/table-crud/base-table-crud.type';

@Component({
  templateUrl: '../../../../shared/formly-forms/types/table-crud/base-table-crud.type.html',
  styleUrls: ['./table-crud-clasificaciones.type.scss']
})
export class TableCRUDClasificacionesTypeComponent extends BaseTableCRUDTypeComponent implements OnInit {

  constructor(
    public tableCRUDMatDialog: MatDialog,
    protected readonly translate: TranslateService,
    private clasificacionService: ClasificacionService
  ) {
    super(tableCRUDMatDialog, translate);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.convertValue(this.model ?? []);
  }

  getMode(): TYPE_RENDER_COLUMN {
    return TYPE_RENDER_COLUMN.CLASIFICATIONS;
  }

  private convertModel(clasificacion: IClasificacion, addToModel: boolean) {
    const customClasificacionModel: IClasificacion = {
      id: clasificacion.id,
      codigo: clasificacion.codigo,
      nombre: clasificacion.nombre,
      padreId: clasificacion.padreId
    };
    const clasificacionModel: any = clasificacion.id;

    if (addToModel) {
      this.add(null, clasificacionModel);
    }
    this.customModel.push(customClasificacionModel);
  }

  private convertValue(arrClasificaciones: string[]) {
    arrClasificaciones.forEach((clasificacionId) => {

      this.clasificacionService.findById(clasificacionId).subscribe((clasificacion) => {
        this.convertModel(clasificacion, false);
        return clasificacion;
      });
    });
  }

  public editItem(rowIndex: number): void {
    // NOT_USED
  }

  public removeItem(rowIndex: number): void {
    super.remove(rowIndex);
    this.customModel.splice(rowIndex, 1);
    this.table.renderRows();
  }

  openDialog() {
    const arrSelectedClasificaciones: IClasificacion[] = this.model.map(item => {
      return {
        id: item,
        codigo: null,
        nombre: null,
        padreId: null
      };
    });

    const data: ClasificacionDataModal = {
      selectedClasificaciones: arrSelectedClasificaciones,
      tipoClasificacion: TipoClasificacion.SECTORES_INDUSTRIALES
    };

    const config = {
      data
    };
    const dialogRef = this.tableCRUDMatDialog.open(ClasificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (clasificaciones) => {
        if (clasificaciones && clasificaciones.length > 0) {
          clasificaciones.forEach((clasificacionItem) => this.convertModel(clasificacionItem.nivelSeleccionado, true));
        }
      }
    );
  }
}
