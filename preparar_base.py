# -*- coding: utf-8 -*-
"""
Prepara la base de datos del backend en un solo paso.

Hace dos cosas que son obligatorias antes de poder usar la API:

  1. Crea la base de datos SQLite a partir de database/create-ecommerce.sql
     (borra la anterior si existe, porque el SQL usa CREATE TABLE sin
     IF NOT EXISTS y fallaria sobre una base ya creada).

  2. Convierte a hash las contrasenas. El SQL las siembra en texto plano,
     pero auth_routes.py las valida con check_password_hash de Werkzeug.
     Un texto plano nunca coincide con un hash, asi que sin este paso el
     login responde 401 aunque escribas la clave correcta.

Uso, desde la carpeta raiz del proyecto y con el entorno virtual activo:

    python preparar_base.py
"""
import os
import sqlite3
import sys

from werkzeug.security import generate_password_hash, check_password_hash

RUTA_SQL = os.path.join("database", "create-ecommerce.sql")
RUTA_DB = os.path.join("database", "ecommerce.db")

USUARIOS_DE_PRUEBA = [
    ("admin", "admin123"),
    ("juan_perez", "password123"),
]


def paso(numero, texto):
    print("\n[%d] %s" % (numero, texto))
    print("-" * 60)


def crear_base():
    paso(1, "Creando la base de datos")

    if not os.path.exists(RUTA_SQL):
        print("    No encuentro %s" % RUTA_SQL)
        print("    Ejecuta este script desde la carpeta raiz del proyecto.")
        return False

    if os.path.exists(RUTA_DB):
        os.remove(RUTA_DB)
        print("    Base anterior borrada.")

    conexion = sqlite3.connect(RUTA_DB)
    with open(RUTA_SQL, encoding="utf-8") as archivo:
        conexion.executescript(archivo.read())
    conexion.commit()

    for tabla in ("Country", "States", "City", "Users", "RoleS", "Product"):
        try:
            total = conexion.execute("SELECT COUNT(*) FROM " + tabla).fetchone()[0]
            print("    %-10s %d registros" % (tabla, total))
        except sqlite3.Error:
            pass

    conexion.close()
    return True


def ya_tiene_hash(valor):
    return valor.startswith("pbkdf2:") or valor.startswith("scrypt:")


def hashear_contrasenas():
    paso(2, "Convirtiendo las contrasenas a hash")

    conexion = sqlite3.connect(RUTA_DB)
    cursor = conexion.cursor()
    cursor.execute("SELECT iD_User, UserName, PasswoRDkey FROM Users")

    cambiadas = 0
    for id_usuario, nombre, password in cursor.fetchall():
        if ya_tiene_hash(password):
            print("    %-14s ya tenia hash" % nombre)
            continue
        nuevo = generate_password_hash(password, method="pbkdf2:sha256", salt_length=16)
        cursor.execute(
            "UPDATE Users SET PasswoRDkey = ? WHERE iD_User = ?",
            (nuevo, id_usuario),
        )
        print("    %-14s '%s' -> %s..." % (nombre, password, nuevo[:32]))
        cambiadas += 1

    conexion.commit()
    print("\n    Contrasenas actualizadas: %d" % cambiadas)
    conexion.close()


def verificar():
    paso(3, "Verificando que el login vaya a funcionar")

    conexion = sqlite3.connect(RUTA_DB)
    cursor = conexion.cursor()
    todo_bien = True

    for nombre, password in USUARIOS_DE_PRUEBA:
        cursor.execute("SELECT PasswoRDkey FROM Users WHERE UserName = ?", (nombre,))
        fila = cursor.fetchone()
        if not fila:
            print("    %-14s NO EXISTE" % nombre)
            todo_bien = False
            continue

        correcto = check_password_hash(fila[0], password)
        erroneo = check_password_hash(fila[0], "clave-que-no-es")
        print("    %-14s password correcto -> %s   password erroneo -> %s"
              % (nombre, correcto, erroneo))
        if not correcto or erroneo:
            todo_bien = False

    conexion.close()
    return todo_bien


def main():
    print("=" * 60)
    print(" PREPARACION DE LA BASE DE DATOS DEL BACKEND")
    print("=" * 60)

    if not crear_base():
        sys.exit(1)

    hashear_contrasenas()

    if verificar():
        print("\n" + "=" * 60)
        print(" LISTO. Ahora arranca el servidor con:  python app.py")
        print("=" * 60)
        print("""
 Credenciales para Postman o la app:

   admin@ecommerce.com / admin123      -> es Administrador
   juan@email.com      / password123   -> NO es administrador

 El login espera estas llaves exactas en el cuerpo JSON:

   { "Email": "admin@ecommerce.com", "PasswoRDkey": "admin123" }
""")
    else:
        print("\n  Algo quedo mal. Revisa los mensajes de arriba.")
        sys.exit(1)


if __name__ == "__main__":
    main()
