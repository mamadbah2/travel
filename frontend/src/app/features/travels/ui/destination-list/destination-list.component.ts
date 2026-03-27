import { Component, input } from '@angular/core';
import { DestinationResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-destination-list',
  templateUrl: './destination-list.component.html',
  styleUrl: './destination-list.component.css',
})
export class DestinationListComponent {
  readonly destinations = input.required<DestinationResponse[]>();
}
