using Items as _Items from '../db/data-model';

service Example01Service {
    @restrict: [{grant: ['READ','WRITE'], to: 'Admin'},
                {grant: ['READ'], to: ['Viewer', 'internal-user']}]
    entity Items as projection on _Items;

    action upload(file: LargeBinary @Core.MediaType: 'text/csv') returns String;
}
