import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import { EvaluacionActionService } from '../../evaluacion/evaluacion.action.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SeguimientoEvaluacionComponent } from './seguimiento-evaluacion.component';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { GestionSeguimientoActionService } from '../../gestion-seguimiento/gestion-seguimiento.action.service';

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
        ReactiveFormsModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: SeguimientoFormularioActionService, useClass: GestionSeguimientoActionService },
        EvaluacionActionService,
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
