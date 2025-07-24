import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { SeguimientoEvaluarActionService } from '../../seguimiento/seguimiento-evaluar.action.service';
import { EtiSharedModule } from '../../shared/eti-shared.module';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoDatosMemoriaComponent } from './seguimiento-datos-memoria.component';

describe('SeguimientoDatosMemoriaComponent', () => {
  let component: SeguimientoDatosMemoriaComponent;
  let fixture: ComponentFixture<SeguimientoDatosMemoriaComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SeguimientoDatosMemoriaComponent,
        SeguimientoListadoAnteriorMemoriaComponent
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
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule,
        EtiSharedModule,
        SgpSharedModule
      ],
      providers: [
        { provide: SeguimientoFormularioActionService, useClass: SeguimientoEvaluarActionService },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguimientoDatosMemoriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
