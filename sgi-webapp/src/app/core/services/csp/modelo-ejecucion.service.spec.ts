import { TestBed } from '@angular/core/testing';

import { ModeloEjecucionService } from './modelo-ejecucion.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ModeloEjecucionService', () => {
  let service: ModeloEjecucionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ModeloEjecucionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
