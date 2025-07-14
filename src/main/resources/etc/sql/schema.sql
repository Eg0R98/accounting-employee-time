-- Создание таблицы заказов, если она не существует
CREATE TABLE IF NOT EXISTS orderEntities (
   id UUID NOT NULL,                  -- Уникальный идентификатор заказа (UUID)
   user_id BIGINT NOT NULL,          -- Идентификатор пользователя, оформившего заказ
   CONSTRAINT pk_orders PRIMARY KEY (id)  -- Первичный ключ по полю id
);

-- Создание таблицы товаров, если она не существует
CREATE TABLE IF NOT EXISTS productEntities (
   id UUID NOT NULL,                         -- Уникальный идентификатор товара (UUID)
   name VARCHAR,                             -- Название товара
   discounted_price DECIMAL,                 -- Цена товара с учётом скидки
   total_value_with_discount DECIMAL,        -- Общая стоимость с учётом скидки и количества
   quantity INTEGER NOT NULL,                -- Количество единиц товара
   sale DECIMAL,                             -- Применённая скидка (например, 0.1 для 10%)
   order_id UUID,                            -- Внешний ключ на таблицу заказов
   CONSTRAINT pk_products PRIMARY KEY (id),  -- Первичный ключ по полю id
   CONSTRAINT fk_products_orders FOREIGN KEY (order_id) REFERENCES orderEntities(id)  -- Связь с таблицей orderEntities
);