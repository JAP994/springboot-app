# LEVANTAR API REST EN SPRING BOOT
# DESCARGAR DOCKER Y CREAR CUENTA DOCKER
https://www.docker.com/products/docker-desktop/

# DESCARGAR PROYECTO DESDE GITHUB O GITLAB
https://gitlab.armada.mil.ec/jasanchezp/springboot-app
https://github.com/JAP994/springboot-app

EN CASO DE DESCARGAR PROYECTO POR GITLAB SE DEBE SOLICITAR ACCESOS
# PASOS PARA CLONAR Y CONFIGURAR EL PROYECTO
    1. Descagar e instalar git https://git-scm.com/
    2. Abrir terminal e ingresar a la carpeta donde se va a descargar el proyecto
    3. Ejecutar el comando git clone https://github.com/JAP994/springboot-app.git
    4. Ingresar en la carpeta contenedora del proyecto
    5. Abrir Visual Studio code desde el terminal comando code .
    6. Cambiar el nombre del archivo (.env copy) a .env
    7. En el archivo .env descomentar las variables gobales debe quedar asi
ì   # PostgreSQL
    POSTGRES_USER=tuadmin
    POSTGRES_PASSWORD=123
    POSTGRES_DB=mydb
    # pgAdmin
    PGADMIN_DEFAULT_EMAIL=addb@example.com
    PGADMIN_DEFAULT_PASSWORD=123
    8. Ejecutar el comando   docker compose up -d   para levantar postgres y el api rest
    9. Esperar que descargue todas las imagenes y que levante el Docker File
# INGRESAR A POSTGRES CON PGADMIN
    1. Una vez levantado los servicios con Docker ir al siguiente enlace donde contiene nuestra base de datos localmente http://localhost:5050/login?next=/
    2. Usuario y contraseña son las que configuramos en el archivo .env
        PGADMIN_DEFAULT_EMAIL=addb@example.com
        PGADMIN_DEFAULT_PASSWORD=123
    3. Registramos el servidor postgres
        name: postgres_container
        coneccion
            hostname: postgres_container
            puert: 5432
            Username: tuadmin
            Password: 123
    4. Ya podremos visualizar nuestra base de datos 
# PARA TRAER TODOS LOS CAMBIOS INCLUSO RAMAS
    1. git pull --all
    2. git branch --all
    3. Para cambiarnos a la rama de desarrollo git checkout develop
    4. verificamos en la rama que nos encontramos sea la develop git branch 
    5. Bajamos el servidor docker compose down
    6. Volvemos a levantar todos los servicios docker compose up -d
