import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { DetalleGastoJustificadoComponent } from './detalle-gasto-justificado.component';

describe('DetalleGastoJustificadoComponent', () => {
  let component: DetalleGastoJustificadoComponent;
  let fixture: ComponentFixture<DetalleGastoJustificadoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        DetalleGastoJustificadoComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        LoggerTestingModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        FlexLayoutModule,
        SharedModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalleGastoJustificadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
