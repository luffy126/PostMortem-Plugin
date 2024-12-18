# PostMortem-Plugin
Documentación del Plugin de Minecraft "PostMortem" (Versión 1.16.5+) // Estado: En Desarrollo

## Introducción

Este plugin simple es para dar una mecanica nueva a servidores hardcore estilo "Permadeath", siendo desarrollado especificamente para el de un amigo y está acá para tenerlo archivado y para que sea público para al que le sirva

# **Como Instalarlo**

Para instalar el plugin hay que hacer lo siguiente:
- Descargar el archivo `postmortem.jar` y moverlo a la carpeta `plugins` dentro del directorio de tu servidor. Si no existe creala
- Descargar el plugin `ForcePack` que se puede encontrar en [este link](https://www.spigotmc.org/resources/forcepack.45439/) y moverlo a la misma carpeta `plugins` de tu servidor
- Iniciar tu servidor y apagarlo. Seguramente ForcePack te de un error pero es normal, se arregla con el siguiente paso
- Descargar el archivo `config.yml` y moverlo a la carpeta `plugins\ForcePack`. Seguro te va a pedir confirmación para sobreescribir el archivo existente, dale a aceptar para hacerlo
- Listo! Abre tu servidor y comprueba que tanto el plugin ForcePack y PostMortem estén activados. Puedes hacerlo metiendo el comando `/plugins` dentro del mundo o en la consola del servidor

## Funcionalidades Principales

Este plugin tiene dos principales funciones:

- La capacidad de dar una **segunda vida extra** a cada jugador, de manera que puede revivir si este muere por primera vez, pero a la segunda se ejecuta el baneo del mismo
- La capacidad de que los jugadores puedan **dar esa vida extra** a alguien que ya haya muerto, permitiendole sacrificar su segunda oportunidad a cambio de darle una chance mas a alguien que ya fue baneado (en desarrollo)

## Funcionalidades Secundarias

- Muestra mediante el chat mensajes de muerte y mensajes de advertencia tanto como para el jugador comprometido como para todos los jugadores que estén en el servidor en ese momento
- Pantalla de muerte y sonido personalizado cuando un jugador muere por segunda vez

## Contribuciones y créditos

Si encuentras algún bug o error dentro del plugin puedes abrir un fork o contactarme por Discord sin problema, el nombre de usuario es `metralletass` y siempre tengo los DMs abiertos
