import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ViaProteccionRoutingModule } from '../via-proteccion-routing.module';

import { ViaProteccionListadoComponent } from './via-proteccion-listado.component';

describe('ViaProteccionListadoComponent', () => {
  let component: ViaProteccionListadoComponent;
  let fixture: ComponentFixture<ViaProteccionListadoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ViaProteccionListadoComponent
      ],
      imports: [
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        HttpClientTestingModule,
        LoggerTestingModule,
        RouterTestingModule,
        SharedModule,
        ViaProteccionRoutingModule,
        MaterialDesignModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViaProteccionListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
