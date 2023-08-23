
CREATE DATABASE IF NOT EXISTS restaurant_api;
use restaurant_api;

INSERT INTO menu_items (name, description, image, price, in_stock,enabled, created_time, updated_time)
VALUES
    ('Hawaiian Pizza', 'All-time favourite toppings, Hawaiian pizza in Tropical Hawaii style.', 'https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/menu1.jpg', 300.0,6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Chicken Tom Yum Pizza', 'Best marinated chicken with pineapple and mushroom on Spicy Lemon sauce. Enjoy our tasty Thai style pizza.', 'https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/menu2.jpg', 350.0,6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Xiaolongbao', 'Chinese steamed bun', 'https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/menu3.jpg', 200.0,6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Kimchi', 'Traditional side dish made from salted and fermented vegetables', 'https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/menu4.jpg', 50.0,6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Oolong tea', 'Partially fermented tea grown in the Alishan area', 'https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/menu5.jpg', 30.0,6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Beer', 'Fantastic flavors and authentic regional appeal beer', 'https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/menu6.jpg', 60.0,6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO  additional_details(created_time, updated_time, name, value, menu_item_id)
VALUES
    (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'Country','VietNam',2),
    (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'Status','Hot',2);

INSERT INTO bills(created_time, updated_time, is_paid)
VALUES
    ( '2013-07-25 09:14:46.403000',CURRENT_TIMESTAMP,false),
    ( CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,true),
    ( CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,false);


INSERT INTO order_items(created_time, updated_time, quantity, unit_price, bill_id, menu_item_id)
VALUES
    (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,2,15,1,5),
    (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,2,15,1,2),
    (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,2,15,2,3),
    (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,2,15,3,1);


SELECT b.* FROM bills b
                    JOIN order_items oi ON b.id = oi.bill_id
                    JOIN menu_items mi ON oi.menu_item_id = mi.id
WHERE mi.name like 'Hawaiian Pizza';

select * from menu_items;
select * from additional_details;
select * from bills;
select  * from order_items;
