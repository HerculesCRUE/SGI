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
import { AreaTematicaDatosGeneralesComponent } from '../area-tematica-formulario/area-tematica-datos-generales/area-tematica-datos-generales.component';
import { AreaTematicaActionService } from '../area-tematica.action.service';

import { AreaTematicaEditarComponent } from './area-tematica-editar.component';

describe('AreaTematicaEditarComponent', () => {
  let component: AreaTematicaEditarComponent;
  let fixture: ComponentFixture<AreaTematicaEditarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        AreaTematicaEditarComponent,
        AreaTematicaDatosGeneralesComponent,
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
        AreaTematicaActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AreaTematicaEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

