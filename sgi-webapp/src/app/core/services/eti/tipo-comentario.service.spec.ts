import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule } from '@sgi/framework/auth';

import { TipoComentarioService } from './tipo-comentario.service';

describe('TipoComentarioService', () => {
  let service: TipoComentarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        LoggerTestingModule,
        SgiAuthModule
      ]
    });
    service = TestBed.inject(TipoComentarioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
