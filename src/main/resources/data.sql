insert into categorias (id, tipo, updated_at, created_at, is_active)
values ('bc4cccd0-92d1-49fe-82b5-fda5a33823c2', 'DISNEY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
       ('e1d1931d-4bee-4015-bea7-cff2f60a2cf2', 'PELICULA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
       ('9b2cf180-91f1-45c1-9363-222bf3d132cc', 'OTROS', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, true);



insert into funkos(name, price, quantity, image, categoria_id, created_at, updated_at)
values ('Funko 1', 100,10,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','bc4cccd0-92d1-49fe-82b5-fda5a33823c2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 2', 200,20,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','e1d1931d-4bee-4015-bea7-cff2f60a2cf2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 3', 300,30,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','9b2cf180-91f1-45c1-9363-222bf3d132cc',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 4', 400,40,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','bc4cccd0-92d1-49fe-82b5-fda5a33823c2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 5', 500,50,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','e1d1931d-4bee-4015-bea7-cff2f60a2cf2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 6', 600,60,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','9b2cf180-91f1-45c1-9363-222bf3d132cc',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 7', 700,70,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','bc4cccd0-92d1-49fe-82b5-fda5a33823c2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 8', 800,80,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','e1d1931d-4bee-4015-bea7-cff2f60a2cf2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 9', 900,90,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','9b2cf180-91f1-45c1-9363-222bf3d132cc',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 10', 1000,100,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','bc4cccd0-92d1-49fe-82b5-fda5a33823c2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 11', 1100,110,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','e1d1931d-4bee-4015-bea7-cff2f60a2cf2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 12', 1200,120,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','9b2cf180-91f1-45c1-9363-222bf3d132cc',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 13', 1300,130,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','bc4cccd0-92d1-49fe-82b5-fda5a33823c2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 14', 1400,140,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','e1d1931d-4bee-4015-bea7-cff2f60a2cf2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 15', 1500,150,'https://www.laopticadeantonio.es/wp-content/uploads/2013/12/150x150.gif','9b2cf180-91f1-45c1-9363-222bf3d132cc',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
         ;
