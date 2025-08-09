using { managed } from '@sap/cds/common';

entity Items : managed {
    key id : UUID;
    name : String;
}