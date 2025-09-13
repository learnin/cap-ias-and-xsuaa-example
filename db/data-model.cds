using { managed } from '@sap/cds/common';

entity Items : managed {
    key id : UUID;
    name : String;
}

entity Books {
  key id : Integer @sql.append: 'GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1)';
  title  : String @mandatory;
}
