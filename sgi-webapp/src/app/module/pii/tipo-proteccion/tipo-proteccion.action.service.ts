import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { ActionService } from '@core/services/action-service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { TipoProteccionDatosGeneralesFragment } from './tipo-proteccion-formulario/tipo-proteccion-datos-generales/tipo-proteccion-datos-generales.fragment';
import { TipoProteccionSubtiposFragment } from './tipo-proteccion-formulario/tipo-proteccion-subtipos/tipo-proteccion-subtipos.fragment';

@Injectable()
export class TipoProteccionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    SUBTIPOS: 'subtipos',
  };

  private tipoProteccion: ITipoProteccion;
  private tipoProteccionDatosGenerales: TipoProteccionDatosGeneralesFragment;
  private tipoProteccionSubtipos: TipoProteccionSubtiposFragment;

  get getTipoProteccion() {
    return this.tipoProteccion;
  }

  constructor(
    private readonly logger: NGXLogger,
    private route: ActivatedRoute,
    private tipoProteccionService: TipoProteccionService,
    private readonly snackBarService: SnackBarService
  ) {
    super();
    this.tipoProteccion = {} as ITipoProteccion;
    if (this.route.snapshot.data.tipoProteccion) {
      this.tipoProteccion = this.route.snapshot.data.tipoProteccion;
      this.enableEdit();
    }

    this.tipoProteccionDatosGenerales = new TipoProteccionDatosGeneralesFragment(this.logger, this.tipoProteccion?.id,
      this.tipoProteccionService);
    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.tipoProteccionDatosGenerales);

    this.tipoProteccionSubtipos = new TipoProteccionSubtiposFragment(this.logger, this.tipoProteccion?.id,
      this.tipoProteccionService, this.snackBarService);
    this.addFragment(this.FRAGMENT.SUBTIPOS, this.tipoProteccionSubtipos);
  }

}
