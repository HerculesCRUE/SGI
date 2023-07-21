import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ActionService } from '@core/services/action-service';
import { ColectivoService } from '@core/services/sgp/colectivo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { NGXLogger } from 'ngx-logger';
import { RolEquipoColectivosFragment } from './rol-equipo-formulario/rol-equipo-colectivos/rol-equipo-colectivos.fragment';
import { RolEquipoDatosGeneralesFragment } from './rol-equipo-formulario/rol-equipo-datos-generales/rol-equipo-datos-generales.fragment';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';

@Injectable()
export class RolEquipoActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    ROL_EQUIPO_COLECTIVOS: 'colectivos'
  };

  private rolEquipo: IRolProyecto;
  private datosGenerales: RolEquipoDatosGeneralesFragment;
  private rolEquipoColectivos: RolEquipoColectivosFragment;

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    rolProyectoService: RolProyectoService,
    colectivoService: ColectivoService,
    rolProyectoColectivoService: RolProyectoColectivoService
  ) {
    super();
    this.rolEquipo = {} as IRolProyecto;
    if (route.snapshot.data.rolEquipo) {
      this.rolEquipo = route.snapshot.data.rolEquipo;
      this.enableEdit();
    }

    this.datosGenerales = new RolEquipoDatosGeneralesFragment(logger, this.rolEquipo?.id,
      rolProyectoService);

    this.rolEquipoColectivos = new RolEquipoColectivosFragment(logger, this.rolEquipo?.id,
      rolProyectoService, colectivoService, rolProyectoColectivoService, false);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.ROL_EQUIPO_COLECTIVOS, this.rolEquipoColectivos);
  }

  get getRolEquipo() {
    return this.rolEquipo;
  }

}
