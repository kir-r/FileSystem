create table Catalogue (
ID int,
ParentCatalogueID int,
Name varchar (255) NOT NULL,

create table Files (
ID int,
ParentCatalogueID int,
Name varchar (255) NOT NULL,
Size int);