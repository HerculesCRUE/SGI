import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EvaluacionActionService } from '../../evaluacion/evaluacion.action.service';
import { GestionSeguimientoActionService } from '../../gestion-seguimiento/gestion-seguimiento.action.service';
import { EtiSharedModule } from '../../shared/eti-shared.module';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoEvaluacionComponent } from './seguimiento-evaluacion.component';

describe('SeguimientoEvaluacionComponent', () => {
  let component: SeguimientoEvaluacionComponent;
  let fixture: ComponentFixture<SeguimientoEvaluacionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SeguimientoEvaluacionComponent,
        SeguimientoListadoAnteriorMemoriaComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        EtiSharedModule,
        SgpSharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: SeguimientoFormularioActionService, useClass: GestionSeguimientoActionService },
        EvaluacionActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguimientoEvaluacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
