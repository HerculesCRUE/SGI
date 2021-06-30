import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ContextoProyectoService } from './contexto-proyecto.service';

describe('ContextoProyectoService', () => {
  let service: ContextoProyectoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ContextoProyectoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});