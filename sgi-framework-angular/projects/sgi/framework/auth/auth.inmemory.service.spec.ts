import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthInMemoryService } from './auth.inmemory.service';
import { Routes } from '@angular/router';

describe('Auth.InmemoryService', () => {
  const routes: Routes = [];
  let service: AuthInMemoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthInMemoryService],
      imports: [RouterTestingModule.withRoutes(routes)]
    });
    service = TestBed.inject(AuthInMemoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
