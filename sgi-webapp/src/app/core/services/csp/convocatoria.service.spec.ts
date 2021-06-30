
import { TestBed } from '@angular/core/testing';

import { ConvocatoriaService } from './convocatoria.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ConvocatoriaService', () => {
  let service: ConvocatoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
