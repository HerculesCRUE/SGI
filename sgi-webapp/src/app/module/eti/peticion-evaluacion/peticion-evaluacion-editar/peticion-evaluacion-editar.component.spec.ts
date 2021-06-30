import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { LoggerTestingModule } from 'ngx-logger/testing';

import {
  PeticionEvaluacionTareasListadoComponent,
} from '../peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.component';
import { PeticionEvaluacionEditarComponent } from './peticion-evaluacion-editar.component';

describe('PeticionEvaluacionEditarComponent', () => {
  let component: PeticionEvaluacionEditarComponent;
  let fixture: ComponentFixture<PeticionEvaluacionEditarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PeticionEvaluacionEditarComponent,
        PeticionEvaluacionTareasListadoComponent,
        ActionFooterComponent
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
        RouterTestingModule.withRoutes([]),
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
