using {
    Items as _Items,
    Books as _Books
 } from '../db/data-model';

service Example01Service {
    @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: 'hoge.csv' @Core.ContentDisposition.Type: 'attachment'
    type CsvFile : LargeBinary;

    @restrict: [{grant: ['READ','WRITE'], to: ['Admin', 'internal-user']},
                {grant: ['READ'], to: 'Viewer'}]
    entity Items as projection on _Items;

    @restrict: [{grant: ['READ','WRITE'], to: ['Admin', 'internal-user']},
                {grant: ['READ'], to: 'Viewer'}]
    entity Books as projection on _Books actions {
        // /odata/v4/Example01Service/Books/Example01Service.download()
        function download(in: many $self) returns CsvFile;

        // これだと content-disposition レスポンスヘッダがセットされない
        // function download(in: many $self) returns LargeBinary @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: 'hoge.csv' @Core.ContentDisposition.Type: 'attachment';
    };

    @restrict: [{grant: 'READ', to: ['Admin', 'Viewer', 'internal-user']}]
    entity CsvFileEntity {
        key id : Integer;
        filename : String;

        // /odata/v4/Example01Service/CsvFileEntity(n)/data
        // セットされるレスポンスヘッダは次の通り。content-disposition: attachment; filename*=UTF-8''ファイル名 のようにエンコーディング指定はされない。
        // content-type: text/csv
        // content-disposition: attachment; filename="%E3%81%82%E3%81%84%E3%81%86.csv"
        data : LargeBinary @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: filename @Core.ContentDisposition.Type: 'attachment';
    }

    // /odata/v4/Example01Service/download()
    function download() returns CsvFile;

    // これだと content-disposition レスポンスヘッダがセットされない
    // function download() returns　LargeBinary @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: 'hoge.csv' @Core.ContentDisposition.Type: 'attachment';
}
