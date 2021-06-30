import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionDatosMemoriaComponent } from './evaluacion-datos-memoria.component';
import { EvaluacionEvaluadorActionService } from '../../evaluacion-evaluador/evaluacion-evaluador.action.service';
import { EvaluacionFormularioActionService } from '../evaluacion-formulario.action.service';

describe('EvaluacionDatosMemoriaComponent', () => {
  let component: EvaluacionDatosMemoriaComponent;
  let fixture: ComponentFixture<EvaluacionDatosMemoriaComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionDatosMemoriaComponent,
        EvaluacionListadoAnteriorMemoriaComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: EvaluacionFormularioActionService, useClass: EvaluacionEvaluadorActionService },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionDatosMemoriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
