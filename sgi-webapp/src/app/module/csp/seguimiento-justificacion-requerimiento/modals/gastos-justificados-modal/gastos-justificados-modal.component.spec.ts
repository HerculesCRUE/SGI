import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '@block/header/header.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { GastosJustificadosModalComponent, IGastosJustificadosModalData } from './gastos-justificados-modal.component';

describe('GastosJustificadosModalComponent', () => {
  let component: GastosJustificadosModalComponent;
  let fixture: ComponentFixture<GastosJustificadosModalComponent>;

  beforeEach(waitForAsync(() => {
    const data = {
      requerimientoJustificacion: {
        proyectoProyectoSge: {
          proyecto: {
            id: 1
          }
        }
      },
      selectedGastosRequerimiento: []
    } as IGastosJustificadosModalData;


    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ],
      declarations: [GastosJustificadosModalComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GastosJustificadosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
