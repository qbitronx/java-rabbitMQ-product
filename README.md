# Java RabbitMQ - User & Product Service

Aplicación Spring Boot con arquitectura orientada a eventos usando RabbitMQ. Gestiona usuarios y productos enviando notificaciones asíncronas por email al crear registros.

## Requisitos

- Docker y Docker Compose
- Java 11 (para desarrollo local)
- Maven 3.8+

## Levantar el proyecto

```bash
# Build y arrancar
docker compose up --build

# En segundo plano
docker compose up -d

# Reconstruir sin caché
docker compose down && docker compose build --no-cache && docker compose up -d
```

## URLs

| Servicio | URL |
|----------|-----|
| API | http://localhost:9100 |
| Health check | http://localhost:9100/health |
| RabbitMQ Management | http://localhost:15672 |

RabbitMQ credentials: `guest` / `guest`

---

## Usuarios - `/api/users`

### Crear usuario
```bash
curl -X POST http://localhost:9100/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan Perez","email":"juan@example.com","phone":"123456789"}'
```

### Obtener todos los usuarios
```bash
curl -X GET http://localhost:9100/api/users
```

### Obtener usuario por ID
```bash
curl -X GET http://localhost:9100/api/users/1
```

### Actualizar usuario
```bash
curl -X PUT http://localhost:9100/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan Perez Actualizado","phone":"987654321"}'
```

### Eliminar usuario
```bash
curl -X DELETE http://localhost:9100/api/users/1
```

---

## Productos - `/api/products`

### Crear producto
```bash
curl -X POST http://localhost:9100/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Pro","description":"Laptop de alto rendimiento","price":1299.99,"stock":10,"category":"Electronica"}'
```

### Obtener todos los productos
```bash
curl -X GET http://localhost:9100/api/products
```

### Obtener producto por ID
```bash
curl -X GET http://localhost:9100/api/products/1
```

### Obtener productos por categoría
```bash
curl -X GET http://localhost:9100/api/products/category/Electronica
```

### Actualizar producto
```bash
curl -X PUT http://localhost:9100/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Pro Max","description":"Laptop de alto rendimiento actualizada","price":1499.99,"stock":8,"category":"Electronica"}'
```

### Eliminar producto
```bash
curl -X DELETE http://localhost:9100/api/products/1
```

---

## Flujo de eventos RabbitMQ

### Usuarios
Al crear un usuario se publica un mensaje en `user-exchange` → `email-queue` que dispara un email de bienvenida al usuario.

### Productos
Al crear un producto se publica un mensaje en `product-exchange` → `product-queue` que dispara un email de notificación al administrador (`app.admin.email`).

---

## Configuración

| Propiedad | Descripción | Default |
|-----------|-------------|---------|
| `app.rabbitmq.exchange` | Exchange de usuarios | `user-exchange` |
| `app.rabbitmq.queue` | Cola de emails de usuarios | `email-queue` |
| `app.rabbitmq.product.exchange` | Exchange de productos | `product-exchange` |
| `app.rabbitmq.product.queue` | Cola de notificaciones de productos | `product-queue` |
| `app.admin.email` | Email del administrador | `admin@example.com` |

En Docker se pueden sobreescribir con variables de entorno: `APP_ADMIN_EMAIL`, `APP_RABBITMQ_EXCHANGE`, etc.

---

## Desarrollo local

```bash
mvn clean install
mvn spring-boot:run
```
