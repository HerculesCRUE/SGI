import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TramoRepartoService } from './tramo-reparto.service';

describe('TramoRepartoService', () => {
  let service: TramoRepartoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TramoRepartoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});