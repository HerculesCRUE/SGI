import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SeguimientoDocumentacionComponent } from './seguimiento-documentacion.component';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { SeguimientoEvaluarActionService } from '../../seguimiento/seguimiento-evaluar.action.service';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';

describe('SeguimientoDocumentacionComponent', () => {
  let component: SeguimientoDocumentacionComponent;
  let fixture: ComponentFixture<SeguimientoDocumentacionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SeguimientoDocumentacionComponent,
        DocumentacionMemoriaListadoMemoriaComponent
      ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SeguimientoFormularioActionService, useClass: SeguimientoEvaluarActionService },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguimientoDocumentacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
