using Items as _Items from '../db/data-model';

service Example01Service {
    @restrict: [{grant: ['READ','WRITE'], to: 'Admin'},
                {grant: ['READ'], to: 'Viewer'}]
    entity Items as projection on _Items;
}
