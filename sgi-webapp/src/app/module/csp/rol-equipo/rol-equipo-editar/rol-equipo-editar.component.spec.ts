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
import { RolEquipoDatosGeneralesComponent } from '../rol-equipo-formulario/rol-equipo-datos-generales/rol-equipo-datos-generales.component';
import { RolEquipoActionService } from '../rol-equipo.action.service';

import { RolEquipoEditarComponent } from './rol-equipo-editar.component';

describe('RolEquipoEditarComponent', () => {
  let component: RolEquipoEditarComponent;
  let fixture: ComponentFixture<RolEquipoEditarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        RolEquipoEditarComponent,
        RolEquipoDatosGeneralesComponent,
        ActionFooterComponent]
      ,
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
        RolEquipoActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RolEquipoEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

