import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { EvaluacionDocumentacionComponent } from './evaluacion-documentacion.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { EvaluacionEvaluadorActionService } from '../../evaluacion-evaluador/evaluacion-evaluador.action.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { EvaluacionFormularioActionService } from '../evaluacion-formulario.action.service';

describe('EvaluacionDocumentacionComponent', () => {
  let component: EvaluacionDocumentacionComponent;
  let fixture: ComponentFixture<EvaluacionDocumentacionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionDocumentacionComponent,
        DocumentacionMemoriaListadoMemoriaComponent
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
        { provide: EvaluacionFormularioActionService, useClass: EvaluacionEvaluadorActionService }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionDocumentacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
