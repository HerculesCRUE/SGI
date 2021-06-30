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
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';

import { ModeloEjecucionTipoUnidadGestionComponent } from './modelo-ejecucion-tipo-unidad-gestion.component';
import { LoggerTestingModule } from 'ngx-logger/testing';


describe('ModeloEjecucionTipoUnidadGestionComponent', () => {
  let component: ModeloEjecucionTipoUnidadGestionComponent;
  let fixture: ComponentFixture<ModeloEjecucionTipoUnidadGestionComponent>;


  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ModeloEjecucionTipoUnidadGestionComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        LoggerTestingModule

      ],
      providers: [

        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },

        ModeloEjecucionActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModeloEjecucionTipoUnidadGestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
