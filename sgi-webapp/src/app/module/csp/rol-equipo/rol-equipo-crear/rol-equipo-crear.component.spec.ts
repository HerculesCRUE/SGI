import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { RolEquipoDatosGeneralesComponent } from '../rol-equipo-formulario/rol-equipo-datos-generales/rol-equipo-datos-generales.component';
import { RolEquipoActionService } from '../rol-equipo.action.service';

import { RolEquipoCrearComponent } from './rol-equipo-crear.component';

describe('RolEquipoCrearComponent', () => {
  let component: RolEquipoCrearComponent;
  let fixture: ComponentFixture<RolEquipoCrearComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        RolEquipoCrearComponent,
        RolEquipoDatosGeneralesComponent,
        ActionFooterComponent
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
        FlexLayoutModule,
        SharedModule,
        LoggerTestingModule
      ],
      providers: [
        RolEquipoActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RolEquipoCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

