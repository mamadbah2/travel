import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { ToastContainerComponent } from '../../../../shared/ui/toast-container/toast-container.component';

@Component({
  selector: 'app-layout-shell',
  imports: [RouterOutlet, NavbarComponent, ToastContainerComponent],
  templateUrl: './layout-shell.component.html',
  styleUrl: './layout-shell.component.css',
})
export class LayoutShellComponent {}
