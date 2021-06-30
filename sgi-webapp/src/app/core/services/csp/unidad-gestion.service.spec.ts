import { TestBed } from '@angular/core/testing';

import { UnidadGestionService } from './unidad-gestion.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('UnidadGestionService', () => {
  let service: UnidadGestionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(UnidadGestionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
