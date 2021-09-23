import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { EvaluacionComentariosComponent } from './evaluacion-comentarios.component';
import { EvaluacionActionService } from '../../evaluacion/evaluacion.action.service';
import { EvaluacionFormularioActionService } from '../evaluacion-formulario.action.service';
import { SgiAuthService } from '@sgi/framework/auth';

describe('EvaluacionComentariosComponent', () => {
  let component: EvaluacionComentariosComponent;
  let fixture: ComponentFixture<EvaluacionComentariosComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionComentariosComponent
      ],
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        MaterialDesignModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: EvaluacionFormularioActionService, useClass: EvaluacionActionService },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionComentariosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
