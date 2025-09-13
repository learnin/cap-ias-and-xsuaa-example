using {
    Items as _Items,
    Books as _Books
 } from '../db/data-model';

service Example01Service {
    @restrict: [{grant: ['READ','WRITE'], to: ['Admin', 'internal-user']},
                {grant: ['READ'], to: 'Viewer'}]
    entity Items as projection on _Items;

    @restrict: [{grant: ['READ','WRITE'], to: ['Admin', 'internal-user']},
                {grant: ['READ'], to: 'Viewer'}]
    entity Books as projection on _Books;

    action upload(file: LargeBinary @Core.MediaType: 'text/csv') returns String;
}
