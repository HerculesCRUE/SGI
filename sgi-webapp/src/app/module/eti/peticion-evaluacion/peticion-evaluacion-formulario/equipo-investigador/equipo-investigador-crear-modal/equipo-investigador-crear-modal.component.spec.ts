import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EquipoInvestigadorCrearModalComponent } from './equipo-investigador-crear-modal.component';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';

describe('EquipoInvestigadorCrearModalComponent', () => {
  let component: EquipoInvestigadorCrearModalComponent;
  let fixture: ComponentFixture<EquipoInvestigadorCrearModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EquipoInvestigadorCrearModalComponent],
      imports: [
        RouterTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        ReactiveFormsModule,
        SharedModule,
        SgpSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: {} }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipoInvestigadorCrearModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
