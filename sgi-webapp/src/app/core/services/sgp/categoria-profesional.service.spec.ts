import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CategoriaProfesionalService } from './categoria-profesional.service';

describe('CategoriaProfesionalService', () => {
  let service: CategoriaProfesionalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(CategoriaProfesionalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
