insert into categorias (id, tipo, updated_at, created_at, is_active)
values ('dda55bca-3f46-470b-8621-6a7ab138266a', 'DISNEY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       ('dda55bca-3f46-470b-8621-6a7ab138266b', 'PELICULA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       ('dda55bca-3f46-470b-8621-6a7ab138266c', 'OTROS', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, false);



insert into funkos(name, price, quantity, image, categoria_id, created_at, updated_at)
values ('Funko 1', 100,10,'funko1','dda55bca-3f46-470b-8621-6a7ab138266a',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 2', 200,20,'funko2','dda55bca-3f46-470b-8621-6a7ab138266b',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
         ('Funko 3', 300,30,'funko3','dda55bca-3f46-470b-8621-6a7ab138266c',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

