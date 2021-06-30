import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SeguimientoComentariosComponent } from './seguimiento-comentarios.component';
import { SeguimientoEvaluarActionService } from '../../seguimiento/seguimiento-evaluar.action.service';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';

describe('SeguimientoComentariosComponent', () => {
  let component: SeguimientoComentariosComponent;
  let fixture: ComponentFixture<SeguimientoComentariosComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SeguimientoComentariosComponent],
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
    fixture = TestBed.createComponent(SeguimientoComentariosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
