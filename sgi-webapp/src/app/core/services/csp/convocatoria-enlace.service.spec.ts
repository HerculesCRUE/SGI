import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConvocatoriaEnlaceService } from './convocatoria-enlace.service';

describe('ConvocatoriaEnlaceService', () => {
  let service: ConvocatoriaEnlaceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaEnlaceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});