import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EvaluacionActionService } from '../../evaluacion/evaluacion.action.service';
import { EvaluacionFormularioActionService } from '../evaluacion-formulario.action.service';
import { EvaluacionListadoAnteriorMemoriaComponent } from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionEvaluacionComponent } from './evaluacion-evaluacion.component';


describe('EvaluacionEvaluacionComponent', () => {
  let component: EvaluacionEvaluacionComponent;
  let fixture: ComponentFixture<EvaluacionEvaluacionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionEvaluacionComponent,
        EvaluacionListadoAnteriorMemoriaComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: EvaluacionFormularioActionService, useClass: EvaluacionActionService },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEvaluacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
