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
        // これだと /odata/v4/Example01Service/Books/Example01Service.download() へのアクセスで
        // org.apache.olingo.server.core.ODataHandlerException: not implemented が発生する。
        // https://cap.cloud.sap/docs/cds/cdl#actions-returning-media に記載されているやり方だが CAP Java というか Olingo では未対応か。
        function download(in: many $self) returns CsvFile;

        // これだと /odata/v4/Example01Service/Books/Example01Service.download2() へのアクセスで
        // com.sap.cds.reflect.CdsElementNotFoundException: No element with name 'download' in 'Example01Service.Books' が発生する
        // function download2(in: many $self) returns LargeBinary @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: 'hoge.csv' @Core.ContentDisposition.Type: 'attachment';
    };

    @restrict: [{grant: 'READ', to: ['Admin', 'Viewer', 'internal-user']}]
    entity CsvFileEntity {
        key id : Integer;

        // これは /odata/v4/Example01Service/CsvFileEntity(1)/data へのアクセスでファイルがダウンロードされる。
        // なお、 /odata/v4/Example01Service/CsvFileEntity/data へのアクセスだと The property 'data' must not follow a collection. で400エラーになる。
        virtual data : LargeBinary @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: 'hoge.csv' @Core.ContentDisposition.Type: 'attachment';
    }

    // これだと /odata/v4/Example01Service/download() へのアクセスで org.apache.olingo.server.core.ODataHandlerException: not implemented が発生する
    function download() returns CsvFile;

    // これだと /odata/v4/Example01Service/download() へのアクセスで
    // java.lang.NullPointerException: Cannot invoke "org.apache.olingo.server.api.uri.UriResourcePartTyped.getType()" because the return value of "com.sap.cds.adapter.odata.v4.processors.request.CdsODataRequest.getLastEntityResource(boolean)" is null が発生する
    // function download() returns　LargeBinary @Core.MediaType: 'text/csv' @Core.ContentDisposition.Filename: 'hoge.csv' @Core.ContentDisposition.Type: 'attachment';
}
