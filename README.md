Roboid-1.0
==========

Projet de système embarqué, université d'Avignon 2013/2014

Ce dépôt est destiné avec mon binôme et moi-même, mais s'il peut vous
donner des idées ou des envie, vous êtes les bienvenus.

Outils
------

 * wiringPi     : communication avec les broches GPIO du connecteur P1
 * motion       : permet de faire du streaming à partir de la caméra
   ([mition](http://www.lavrsen.dk/foswiki/bin/view/Motion/WebHome))
 * hotspot wifi :
   [RPI-Wireless-Hotspot](https://github.com/harryallerston/RPI-Wireless-Hotspot)
 * Cross Compiling : [crosstool-ng](http://crosstool-ng.org/)
   * installation sous Fedora : [Attila Kanto's Blog](https://akanto.wordpress.com/2012/10/02/cross-compiling-kernel-for-raspberry-pi-on-fedora-17-part-2/)

Serveur NFS
-----------

Pour faciliter le développement du projet j'ai installé un serveur NFS qui
exporte le dossier /home/pi/Workspace sur le réseau local.
 * sudo mount -t nfs 192.168.x.x:/export/Workspace /home/xxx/Workspace

Premiers Tests
--------------

Le dossier *test* contient un sciprt trés simple pour faire tourner les roues.
Il définie aussi une base (non définitive) de structure pour l'utilisation
des moteurs électriques.

Application Android
-------------------

Le dossier RemoteControl contient les sources de la télécommande du robot.
Elle se présente sous la forme d'un application Android.

Annexes
-------

 * motion.conf : fichier de configuration de motion (pour les version 3.2 et 3.3)
