## Git Befehle

### Grundbefehle:

- **Init:**
  ```sh 
  git init # Ein neues Git-Repository initialisieren
  ```

- **Clone:**
  ```sh
  git clone "url" # Ein Repository von einer URL klonen (herunterladen)
  ```

### Branching & Merging:

- **Branch auflisten:**
  ```sh
  git branch # Branches im Repo auflisten
  ```

- **Neuen Branch erstellen:**
  ```sh
  git branch branch-name # Einen neuen Branch erstellen
  ```

- **Zu einem Branch wechseln:**
  ```sh
  git checkout branch-name # Zu einem Branch wechseln
  ```

- **Einen Branch mergen:**
  ```sh
  git merge branch-name # Einen Branch in den aktiven Branch mergen
  ```

- **Einen Branch löschen:**
  ```sh
  git branch -d branch-name # Einen Branch löschen
  ```

### Änderungen vornehmen:

- **Datei stagen:**
  ```sh
  git add file # Eine Datei für den Commit vorbereiten (stagen)
  ```

- **Alle Änderungen stagen:**
  ```sh
  git add . # Alle Änderungen für den Commit stagen
  ```

- **Committen:**
  ```sh
  git commit -m "commit message" # Vorbereitete Änderungen mit einer Nachricht committen
  ```

- **Unterschiede anzeigen:**
  ```sh
  git diff # Zeige Dateiunterschiede, die noch nicht gestaged sind
  ```

### Dateien entfernen & umbenennen:

- **Datei entfernen:**
  ```sh
  git rm file # Eine Datei löschen und die Änderung stagen
  ```

- **Datei umbenennen:**
  ```sh
  git mv file-original file-renamed # Eine Datei umbenennen und die Änderung stagen
  ```

### Verlauf & Protokoll:

- **Commit-Protokolle anzeigen:**
  ```sh
  git log # Commit-Protokolle anzeigen
  git log --oneline # Commit-Protokolle anzeigen, ein Commit pro Zeile
  ```

- **Blame:**
  ```sh
  git blame file # Zeige, wer was & wann in file geändert hat
  ```

### Remote Repositories:

- **Remote-Repository hinzufügen:**
  ```sh
  git remote add alias url # Ein Remote-Repository hinzufügen
  ```

- **Remote-Repository entfernen:**
  ```sh
  git remote remove alias # Ein Remote-Repository entfernen
  ```

- **Änderungen abrufen:**
  ```sh
  git fetch alias # Änderungen von einem Remote-Repository abrufen
  ```

- **Änderungen pushen:**
  ```sh
  git push alias branch # Änderungen zu einem Remote-Repository pushen
  ```

- **Änderungen pullen:**
  ```sh
  git pull alias branch # Änderungen von einem Remote-Repository ziehen (pullen)
  ```

### Stashing & Cleaning:

- **Stash:**
  ```sh
  git stash # Nicht committete Änderungen speichern
  git stash pop # Gespeicherte Änderungen anwenden
  ```

- **Clean:**
  ```sh
  git clean -n # Zeigen, was entfernt wird (Trockenlauf)
  git clean -f # Nicht verfolgte Dateien erzwingend entfernen
  ```

### Änderungen rückgängig machen:

- **Reset:**
  ```sh
  git reset file # Eine Datei unstage lassen, Änderungen beibehalten
  git reset --soft commit # Zum Commit zurücksetzen, gestaged Änderungen beibehalten
  git reset --hard commit # Zum Commit zurücksetzen, alle Änderungen verwerfen
  ```