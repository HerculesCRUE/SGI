import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PubInicioComponent } from './pub-inicio.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('PubInicioComponent', () => {
  let component: PubInicioComponent;
  let fixture: ComponentFixture<PubInicioComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule.withRoutes([])
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PubInicioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

