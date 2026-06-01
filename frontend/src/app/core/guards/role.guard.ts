import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RoleService } from '../services/role.service';

export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return () => {
    const roleService = inject(RoleService);
    const router = inject(Router);
    if (roleService.hasAnyRole(allowedRoles)) {
      return true;
    }
    return router.createUrlTree(['/dashboard'], { queryParams: { accessDenied: '1' } });
  };
}
