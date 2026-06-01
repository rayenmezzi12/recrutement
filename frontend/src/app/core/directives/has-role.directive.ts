import { Directive, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { RoleService } from '../services/role.service';

@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class HasRoleDirective implements OnInit {
  @Input('appHasRole') allowedRoles: string[] = [];

  constructor(
    private templateRef: TemplateRef<unknown>,
    private viewContainer: ViewContainerRef,
    private roleService: RoleService
  ) {}

  ngOnInit(): void {
    if (this.roleService.hasAnyRole(this.allowedRoles)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
