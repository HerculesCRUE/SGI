import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ViaProteccionService } from './via-proteccion.service';

describe('ViaProteccionService', () => {
  let service: ViaProteccionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ViaProteccionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
