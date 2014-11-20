# README #

Alle News und Diskusionen auf Hipchat: https://ppati000.hipchat.com/invite/189128/38fb4d458cbdb0e88838f20b2e1c4521

SimpleClient

Was machen Sachen und wie geht das
=========


1. **Git** installieren (http://git-scm.com/downloads)
2. **Git Bash** starten (falls du es nicht findest einfach nach "Git Bash" suchen)
3. Nun müssen wir einen **SSH Key** generieren
    - `ssh-keygen` eingeben
    - Bei der Frage, wo es gespeichert werden soll, einfach Enter drücken
    - Bei der Frage nach einer Passphrase auch einfach zweimal Enter
    - Fertig!
4. Nun fügen wir unseren Key auf BitBucket hinzu
    - Auf Bitbucket oben rechts auf dein Profil und dann **Manage Account**
    - Unter "Security" auf "SSH Keys"
    - Auf "Add Key" klicken
    - Irgendwas als Label eingeben, z.B "Marcels PC"
    - Darunter den **gesamten Inhalt** von `C:\Users\(dein benutzername)\.ssh\id_rsa.pub` pasten
    - Ok, nun zurück in die Git Bash

5. Einen neuen Ordner für den Code machen
    - z.B. `mkdir ~/Documents/SimpleClient/ && cd ~/Documents/SimpleClient/`

6. Nun müssen wir die Repository von Bitbucket *clonen*, d.h. man lädt sie runter, damit man eine eigene, lokale Version des Codes bei sich auf dem PC hat.
    - `git clone git@bitbucket.org:ppati000/simpleclient.git`

7. Der Code ist jetzt in Dokumente/SimpleClient. Jetzt kannst du alles mit deinem Editor öffnen
8. Bevor wir etwas neues in den Code einbauen, **wechseln wir zu einer neuen Branch**: `git checkout -b irgendein-neues-feature` (in Git Bash eingeben)
9. Jetzt kannst du loslegen mit coden :D
10. Wenn du irgendwas fertig hast, dann musst du einen *commit* machen, d.h. die Änderungen in deiner lokalen Repo festhalten:
    - z.B.`git commit -a -m "Update RandomLogic to kill all enemy players to win the game"`
11. Nun sind die Änderungen am Code auf der Repo auf Deinem PC gespeichert. Damit sie auf BitBucket landen, musst du sie noch einen *push* machen: 
    - `git push`

12. Nun sind die Änderungen in BitBucket auf der Branch irgendein-neues-feature. Wenn du meinst, der Code ist jetzt gut genug, um in die Haupt-Branch ("Master") eingebracht zu werden, erstelle ein *Pull Request* in BitBucket:
    - Auf **Pull Requests** gehen
    - Links deine Branch, rechts Master auswählen

13. jetzt können ich (und Lennart falls der mal kommt) den Code anschauen, testen und dann kommt er in die *master* Branch ^_^

Zusammenfassung des Workflows
-----------------

 - Zu einer **neuen Branch** wechseln (`git checkout -b name-der-branch`)
 - **Coden**
 - Änderungen an die lokale Repo **committen** (`git commit -a -m "hier beschreiben was neu ist"`)

Wenn dein Code fertig/gut genug ist:

 - Änderungen **pushen** (`git push`)
 - **Pull Request** machen
 - **Smoke crack** (optional)

### Alte Anleitung (für NetBeans) ###

* Erstmal auf Bitbucket unter Manage Profile auf SSH Keys gehen und einen hochladen (falls du noch keinen hast, musst du zuerst einen generieren, wird alles erklärt)
* In NetBeans: Neues Projekt erstellen, "Create Main Class" deaktivieren
* Dann im Menü Team > Git > Clone
* URL: bitbucket.org:ppati000/simpleclient.git
* Bei Authentication auf Private/Public key gehen und den private key (ohne .pub) auswählen
* Fertig!


### Und wenn ich nur die KI ausprobieren möchte? ###

https://bitbucket.org/ppati000/simpleclient/downloads/patrick_player.zip